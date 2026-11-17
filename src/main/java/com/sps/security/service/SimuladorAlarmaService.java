package com.sps.security.service;

import com.sps.security.dao.DispositivoDAO;
import com.sps.security.dao.impl.DispositivoDAOImpl;
import com.sps.security.model.enums.NivelPrioridad;
import com.sps.security.model.telemetry.Dispositivo;
import com.sps.security.util.TareasBackground;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SimuladorAlarmaService {
    private final DispositivoDAO dispositivoDAO;
    private final AlarmaService alarmaService;
    private volatile boolean activo;

    public SimuladorAlarmaService() { this(new DispositivoDAOImpl(), new AlarmaService()); }
    public SimuladorAlarmaService(DispositivoDAO dao, AlarmaService service) {
        dispositivoDAO = dao; alarmaService = service;
    }
    public synchronized void iniciar(long segundos) {
        if (segundos <= 0) throw new IllegalArgumentException("El periodo debe ser mayor que cero.");
        if (activo) return;
        activo = true;
        TareasBackground.programar(this::generarAlarma, segundos);
        TareasBackground.registrarCierre(this::detener);
    }
    public synchronized void detener() { activo = false; }
    public boolean estaActivo() { return activo; }
    public void generarAlarma() {
        if (!activo) return;
        List<Dispositivo> dispositivos = dispositivoDAO.obtenerTodos().stream()
                .filter(d -> Boolean.TRUE.equals(d.getEstadoActividad())).toList();
        if (dispositivos.isEmpty()) return;
        Dispositivo d = dispositivos.get(ThreadLocalRandom.current().nextInt(dispositivos.size()));
        String[] tipos = {"PIR_MOVIMIENTO", "MAGNETICO_PUERTA", "TEMPERATURA", "INCENDIO"};
        NivelPrioridad[] prioridades = NivelPrioridad.values();
        alarmaService.registrar(d.getIdDispositivo(),
                tipos[ThreadLocalRandom.current().nextInt(tipos.length)],
                prioridades[ThreadLocalRandom.current().nextInt(prioridades.length)],
                "Alarma generada por el simulador.");
    }
}
