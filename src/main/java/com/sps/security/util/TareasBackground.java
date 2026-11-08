package com.sps.security.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TareasBackground {

    private static ScheduledExecutorService scheduler = crearScheduler();
    private static final List<Runnable> accionesDeCierre = new CopyOnWriteArrayList<>();

    private TareasBackground() {}

    private static ScheduledExecutorService crearScheduler(){
        return Executors.newScheduledThreadPool(2 , r -> {
            Thread t = new Thread(r, "sps-worker");
            t.setDaemon(true);
            return t;
        });
    }

    /** Ejecuta una tarea de forma periódica en segundo plano. */
    public static void programar(Runnable tarea, long periodoSegundos){
        scheduler.scheduleAtFixedRate(() -> {
            try {
                tarea.run();
            } catch (Exception e) {
                // Si una ejecución lanza excepción, el scheduler cancela las siguientes en silencio.
                e.printStackTrace();
            }
        }, 0, periodoSegundos, TimeUnit.SECONDS);
    }

    /** Registra algo que hay que cerrar al detener todo (socket, Timeline, etc.). */
    public static void registrarCierre(Runnable accion) {
        accionesDeCierre.add(accion);
    }

    /** Apaga todo y deja la clase lista para una nueva sesión. */
    public static synchronized void detenerTodo() {
        scheduler.shutdownNow();                 // interrumpe los hilos del pool
        accionesDeCierre.forEach(accion -> {
            try { accion.run(); } catch (Exception e) { e.printStackTrace(); }
        });
        accionesDeCierre.clear();
        scheduler = crearScheduler();            // pool nuevo para el próximo login
    }


}
