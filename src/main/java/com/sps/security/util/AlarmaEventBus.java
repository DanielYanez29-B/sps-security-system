package com.sps.security.util;

import com.sps.security.model.telemetry.Alarma;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class AlarmaEventBus {
    private static final List<Consumer<Alarma>> listeners = new CopyOnWriteArrayList<>();

    private AlarmaEventBus() {}

    public static void suscribir(Consumer<Alarma> listener) {
        listeners.add(listener);
    }

    public static void cancelar(Consumer<Alarma> listener) {
        listeners.remove(listener);
    }

    public static void publicar(Alarma alarma) {
        listeners.forEach(listener -> listener.accept(alarma));
    }
}
