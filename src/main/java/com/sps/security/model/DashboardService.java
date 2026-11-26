package com.sps.security.service;

import com.sps.security.dao.*;
import com.sps.security.dao.impl.*;
import com.sps.security.model.enums.EstadoAlarma;
import com.sps.security.model.enums.NivelPrioridad;
import com.sps.security.model.operative.Asignacion;
import com.sps.security.model.operative.Jornada;
import com.sps.security.model.telemetry.Alarma;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardService {

    public record Resumen(
            long alarmasPendientes,
            long guardiasEnTurno,
            long dispositivosActivos,
            long notificacionesNoLeidas,
            List<Alarma> ultimasAlarmas,
            Map<NivelPrioridad, Long> pendientesPorPrioridad) {}

    private final AlarmaDAO alarmaDAO;
    private final DispositivoDAO dispositivoDAO;
    private final AsignacionDAO asignacionDAO;
    private final JornadaDAO jornadaDAO;
    private final NotificacionDAO notificacionDAO;

    public DashboardService() {
        this(new AlarmaDAOImpl(), new DispositivoDAOImpl(),
                new AsignacionDAOImpl(), new JornadaDAOImpl(),
                new NotificacionDAOImpl());
    }

    public DashboardService(AlarmaDAO a, DispositivoDAO d, AsignacionDAO as,
                            JornadaDAO j, NotificacionDAO n) {
        this.alarmaDAO = a; this.dispositivoDAO = d;
        this.asignacionDAO = as; this.jornadaDAO = j; this.notificacionDAO = n;
    }

    public Resumen obtenerResumen(Integer idUsuario) {
        List<Alarma> alarmas = alarmaDAO.obtenerTodos(); // ya vienen DESC por fecha

        List<Alarma> pendientes = alarmas.stream()
                .filter(a -> a.getEstado() == EstadoAlarma.PENDIENTE)
                .toList();

        Map<NivelPrioridad, Long> porPrioridad = new EnumMap<>(NivelPrioridad.class);
        for (NivelPrioridad p : NivelPrioridad.values()) porPrioridad.put(p, 0L);
        pendientes.forEach(a -> porPrioridad.merge(a.getNivelPrioridad(), 1L, Long::sum));

        long dispositivos = dispositivoDAO.obtenerTodos().stream()
                .filter(d -> Boolean.TRUE.equals(d.getEstadoActividad()))
                .count();

        long noLeidas = idUsuario == null ? 0
                : notificacionDAO.obtenerNoLeidasPorUsuario(idUsuario).size();

        return new Resumen(
                pendientes.size(),
                contarGuardiasEnTurno(),
                dispositivos,
                noLeidas,
                alarmas.stream().limit(5).toList(),
                porPrioridad);
    }

    /** Guardias cuya asignación (fecha + jornada) incluye el momento actual. */
    private long contarGuardiasEnTurno() {
        LocalDateTime ahora = LocalDateTime.now();
        Map<Integer, Jornada> jornadas = jornadaDAO.obtenerTodos().stream()
                .collect(Collectors.toMap(Jornada::getIdJornada, j -> j));

        Set<Integer> enTurno = new HashSet<>();
        for (Asignacion a : asignacionDAO.obtenerTodos()) {
            Jornada j = jornadas.get(a.getIdJornada());
            if (j == null || a.getFechaAsignacion() == null) continue;

            LocalDateTime inicio = LocalDateTime.of(a.getFechaAsignacion(), j.getHoraInicio());
            LocalDateTime fin = LocalDateTime.of(a.getFechaAsignacion(), j.getHoraFin());
            if (!fin.isAfter(inicio)) fin = fin.plusDays(1); // turno nocturno

            if (!ahora.isBefore(inicio) && ahora.isBefore(fin)) {
                enTurno.add(a.getIdGuardia());
            }
        }
        return enTurno.size();
    }
}