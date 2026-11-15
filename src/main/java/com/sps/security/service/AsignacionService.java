package com.sps.security.service;

import com.sps.security.dao.AsignacionDAO;
import com.sps.security.dao.impl.AsignacionDAOImpl;
import com.sps.security.model.operative.Asignacion;
import com.sps.security.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AsignacionService {

    private final AsignacionDAO asignacionDAO;
    private final ValidadorReferencias validador;

    public AsignacionService() {
        this(new AsignacionDAOImpl(), new ValidadorSQL());
    }

    public AsignacionService(
            AsignacionDAO asignacionDAO,
            ValidadorReferencias validador) {

        this.asignacionDAO = Objects.requireNonNull(asignacionDAO);
        this.validador = Objects.requireNonNull(validador);
    }

    public void registrarAsignacion(
            Integer idGuardia,
            Integer idServicio,
            Integer idJornada,
            LocalDate fechaAsignacion) {

        validarDatos(
                idGuardia,
                idServicio,
                idJornada,
                fechaAsignacion
        );

        validarReferencias(
                idGuardia,
                idServicio,
                idJornada
        );

        validarDuplicados(
                idGuardia,
                idJornada,
                fechaAsignacion,
                null
        );

        Asignacion asignacion = new Asignacion(
                null,
                idGuardia,
                idServicio,
                idJornada,
                fechaAsignacion
        );

        if (!asignacionDAO.guardar(asignacion)) {
            throw new IllegalStateException(
                    "No se pudo registrar la asignación."
            );
        }
    }

    public void actualizarAsignacion(
            Integer idAsignacion,
            Integer idGuardia,
            Integer idServicio,
            Integer idJornada,
            LocalDate fechaAsignacion) {

        Asignacion asignacion =
                buscarAsignacionExistente(idAsignacion);

        validarDatos(
                idGuardia,
                idServicio,
                idJornada,
                fechaAsignacion
        );

        validarReferencias(
                idGuardia,
                idServicio,
                idJornada
        );

        validarDuplicados(
                idGuardia,
                idJornada,
                fechaAsignacion,
                idAsignacion
        );

        asignacion.setIdGuardia(idGuardia);
        asignacion.setIdServicio(idServicio);
        asignacion.setIdJornada(idJornada);
        asignacion.setFechaAsignacion(fechaAsignacion);

        if (!asignacionDAO.actualizar(asignacion)) {
            throw new IllegalStateException(
                    "No se pudo actualizar la asignación."
            );
        }
    }

    public void eliminarAsignacion(Integer idAsignacion) {

        Asignacion asignacion =
                buscarAsignacionExistente(idAsignacion);

        if (!asignacionDAO.eliminar(asignacion)) {
            throw new IllegalStateException(
                    "No se pudo eliminar la asignación."
            );
        }
    }

    public List<Asignacion> listarAsignaciones() {
        return asignacionDAO.obtenerTodos();
    }

    public List<Asignacion> listarAsignacionesPorGuardia(
            Integer idGuardia) {

        validarId(idGuardia, "guardia");

        return asignacionDAO.obtenerAsignacionesPorGuardia(
                idGuardia
        );
    }

    public Optional<Asignacion> obtenerAsignacionPorId(
            Integer idAsignacion) {

        validarId(idAsignacion, "asignación");

        return asignacionDAO.obtenerPorId(idAsignacion);
    }

    private Asignacion buscarAsignacionExistente(
            Integer idAsignacion) {

        validarId(idAsignacion, "asignación");

        return asignacionDAO.obtenerPorId(idAsignacion)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la asignación con ID "
                                + idAsignacion
                ));
    }

    private void validarDatos(
            Integer idGuardia,
            Integer idServicio,
            Integer idJornada,
            LocalDate fechaAsignacion) {

        validarId(idGuardia, "guardia");
        validarId(idServicio, "servicio");
        validarId(idJornada, "jornada");

        if (fechaAsignacion == null) {
            throw new IllegalArgumentException(
                    "La fecha de asignación es obligatoria."
            );
        }
    }

    private void validarId(Integer id, String nombre) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de " + nombre
                            + " debe ser un número positivo."
            );
        }
    }

    private void validarReferencias(
            Integer idGuardia,
            Integer idServicio,
            Integer idJornada) {

        if (!validador.guardiaActivo(idGuardia)) {
            throw new IllegalArgumentException(
                    "El guardia no existe o está inactivo."
            );
        }

        if (!validador.servicioActivo(idServicio)) {
            throw new IllegalArgumentException(
                    "El servicio no existe o no está activo."
            );
        }

        if (!validador.jornadaActiva(idJornada)) {
            throw new IllegalArgumentException(
                    "La jornada no existe o está inactiva."
            );
        }
    }

    private void validarDuplicados(
            Integer idGuardia,
            Integer idJornada,
            LocalDate fechaAsignacion,
            Integer idAsignacionActual) {

        List<Asignacion> asignaciones =
                asignacionDAO.obtenerAsignacionesPorGuardia(
                        idGuardia
                );

        for (Asignacion existente : asignaciones) {

            boolean esOtraAsignacion =
                    !Objects.equals(
                            existente.getIdAsignacion(),
                            idAsignacionActual
                    );

            boolean mismaJornada = Objects.equals(
                    existente.getIdJornada(),
                    idJornada
            );

            boolean mismaFecha = Objects.equals(
                    existente.getFechaAsignacion(),
                    fechaAsignacion
            );

            if (esOtraAsignacion
                    && mismaJornada
                    && mismaFecha) {

                throw new IllegalArgumentException(
                        "El guardia ya tiene una asignación "
                                + "en esa fecha y jornada."
                );
            }
        }
    }

    public interface ValidadorReferencias {

        boolean guardiaActivo(Integer idGuardia);

        boolean servicioActivo(Integer idServicio);

        boolean jornadaActiva(Integer idJornada);
    }

    private static class ValidadorSQL
            implements ValidadorReferencias {

        @Override
        public boolean guardiaActivo(Integer idGuardia) {

            return existeRegistro(
                    "SELECT 1 FROM GUARDIA "
                            + "WHERE id_guardia = ? "
                            + "AND estatus_activo = TRUE",
                    idGuardia
            );
        }

        @Override
        public boolean servicioActivo(Integer idServicio) {

            return existeRegistro(
                    "SELECT 1 FROM SERVICIO "
                            + "WHERE id_servicio = ? "
                            + "AND UPPER(estado) = 'ACTIVO'",
                    idServicio
            );
        }

        @Override
        public boolean jornadaActiva(Integer idJornada) {

            return existeRegistro(
                    "SELECT 1 FROM JORNADA "
                            + "WHERE id_jornada = ? "
                            + "AND estado_actividad = TRUE",
                    idJornada
            );
        }

        private boolean existeRegistro(
                String sql,
                Integer id) {

            try (Connection conn =
                         DatabaseConnection.getConnection();
                 PreparedStatement stmt =
                         conn.prepareStatement(sql)) {

                stmt.setInt(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }

            } catch (SQLException e) {
                throw new IllegalStateException(
                        "Error al validar los datos de "
                                + "la asignación: "
                                + e.getMessage(),
                        e
                );
            }
        }
    }
}
