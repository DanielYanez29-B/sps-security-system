package com.sps.security.service;

import com.sps.security.dao.NotificacionDAO;
import com.sps.security.model.telemetry.Alarma;
import com.sps.security.model.telemetry.Notificacion;
import com.sps.security.model.users.Usuario;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class NotificacionService {
    private final NotificacionDAO dao;

    public NotificacionService() { this(new com.sps.security.dao.impl.NotificacionDAOImpl()); }
    public NotificacionService(NotificacionDAO dao) { this.dao = Objects.requireNonNull(dao); }

    public void notificarUsuariosActivos(Alarma alarma, List<Usuario> usuarios) {
        for (Usuario usuario : usuarios) {
            if (Boolean.TRUE.equals(usuario.getEstadoActivo())
                    && (usuario.getIdRol() == 1 || usuario.getIdRol() == 2)) {
                Notificacion n = new Notificacion(null, alarma.getIdAlarma(),
                        usuario.getIdUsuario(), LocalDateTime.now(),
                        "Alarma " + alarma.getNivelPrioridad() + ": " + alarma.getTipoAlarma(), false);
                if (!dao.guardar(n)) throw new IllegalStateException("No se pudo crear una notificación.");
            }
        }
    }

    public List<Notificacion> listarPorUsuario(Integer idUsuario) {
        validarId(idUsuario); return dao.obtenerPorUsuario(idUsuario);
    }
    public List<Notificacion> listarNoLeidas(Integer idUsuario) {
        validarId(idUsuario); return dao.obtenerNoLeidasPorUsuario(idUsuario);
    }
    public void marcarComoLeida(Integer id) { validarId(id); if (!dao.marcarComoLeida(id)) throw new IllegalStateException("No se pudo marcar la notificación."); }
    public void marcarTodasComoLeidas(Integer idUsuario) { validarId(idUsuario); dao.marcarTodasComoLeidas(idUsuario); }
    private void validarId(Integer id) { if (id == null || id <= 0) throw new IllegalArgumentException("El ID debe ser válido."); }
}
