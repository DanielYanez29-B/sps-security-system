package com.sps.security.service;

import com.sps.security.dao.AlarmaDAO;
import com.sps.security.dao.DispositivoDAO;
import com.sps.security.dao.UsuarioDAO;
import com.sps.security.dao.impl.AlarmaDAOImpl;
import com.sps.security.dao.impl.DispositivoDAOImpl;
import com.sps.security.dao.impl.NotificacionDAOImpl;
import com.sps.security.dao.impl.UsuarioDAOImpl;
import com.sps.security.model.enums.EstadoAlarma;
import com.sps.security.model.enums.NivelPrioridad;
import com.sps.security.model.telemetry.Alarma;
import com.sps.security.model.telemetry.Dispositivo;
import com.sps.security.model.telemetry.Notificacion;
import com.sps.security.model.users.Usuario;
import com.sps.security.util.AlarmaEventBus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AlarmaService {
    private static final List<String> TIPOS = List.of(
            "PIR_MOVIMIENTO", "MAGNETICO_PUERTA", "TEMPERATURA",
            "ROTURA_CRISTAL", "INCENDIO", "SISMO", "HUMEDAD");

    private final AlarmaDAO alarmaDAO;
    private final DispositivoDAO dispositivoDAO;
    private final UsuarioDAO usuarioDAO;
    private final NotificacionService notificacionService;

    public AlarmaService() {
        this(new AlarmaDAOImpl(), new DispositivoDAOImpl(),
                new UsuarioDAOImpl(), new NotificacionService(new NotificacionDAOImpl()));
    }

    public AlarmaService(AlarmaDAO alarmaDAO, DispositivoDAO dispositivoDAO,
                         UsuarioDAO usuarioDAO, NotificacionService notificacionService) {
        this.alarmaDAO = Objects.requireNonNull(alarmaDAO);
        this.dispositivoDAO = Objects.requireNonNull(dispositivoDAO);
        this.usuarioDAO = Objects.requireNonNull(usuarioDAO);
        this.notificacionService = Objects.requireNonNull(notificacionService);
    }

    public Alarma registrar(Integer idDispositivo, String tipo, NivelPrioridad prioridad,
                            String descripcion) {
        Dispositivo dispositivo = dispositivoDAO.obtenerPorId(idDispositivo)
                .orElseThrow(() -> new IllegalArgumentException("El dispositivo no existe."));
        if (!Boolean.TRUE.equals(dispositivo.getEstadoActividad()))
            throw new IllegalArgumentException("El dispositivo está inactivo.");
        if (tipo == null || !TIPOS.contains(tipo.trim().toUpperCase(Locale.ROOT)))
            throw new IllegalArgumentException("El tipo de alarma no es válido.");
        if (prioridad == null) throw new IllegalArgumentException("La prioridad es obligatoria.");

        Alarma alarma = new Alarma(null, idDispositivo, LocalDateTime.now(),
                prioridad, tipo.trim().toUpperCase(Locale.ROOT),
                EstadoAlarma.PENDIENTE, normalizar(descripcion));
        if (!alarmaDAO.guardar(alarma)) throw new IllegalStateException("No se pudo registrar la alarma.");
        notificacionService.notificarUsuariosActivos(alarma, usuarioDAO.obtenerTodos());
        AlarmaEventBus.publicar(alarma);
        return alarma;
    }

    public void cambiarEstado(Integer idAlarma, EstadoAlarma nuevoEstado) {
        if (nuevoEstado == null || idAlarma == null || idAlarma <= 0)
            throw new IllegalArgumentException("La alarma y el estado son obligatorios.");
        Alarma alarma = alarmaDAO.obtenerPorId(idAlarma)
                .orElseThrow(() -> new IllegalArgumentException("La alarma no existe."));
        if (alarma.getEstado() != EstadoAlarma.PENDIENTE)
            throw new IllegalArgumentException("Solo se puede cambiar una alarma pendiente.");
        alarma.setEstado(nuevoEstado);
        if (!alarmaDAO.actualizar(alarma))
            throw new IllegalStateException("No se pudo actualizar el estado de la alarma.");
    }

    public List<Alarma> listar() { return alarmaDAO.obtenerTodos(); }
    public List<Alarma> filtrar(java.time.LocalDateTime desde, java.time.LocalDateTime hasta,
                                Integer dispositivo, NivelPrioridad prioridad, EstadoAlarma estado) {
        if (desde != null && hasta != null && desde.isAfter(hasta))
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final.");
        return alarmaDAO.obtenerFiltradas(desde, hasta, dispositivo, prioridad, estado);
    }

    private String normalizar(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
