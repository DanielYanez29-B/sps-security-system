package com.sps.security.dao.impl;

import com.sps.security.dao.AsignacionDAO;
import com.sps.security.model.operative.Asignacion;
import com.sps.security.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AsignacionDAOImpl implements AsignacionDAO {

    private static final String COLUMNAS =
            "id_asignacion, id_guardia, id_servicio, "
                    + "id_jornada, fecha_asignacion";

    @Override
    public boolean guardar(Asignacion entidad) {

        String sql = """
                INSERT INTO ASIGNACION
                (id_guardia, id_servicio, id_jornada,
                 fecha_asignacion)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            asignarParametros(stmt, entidad);

            if (stmt.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entidad.setIdAsignacion(keys.getInt(1));
                }
            }

            return true;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al registrar la asignación: "
                            + e.getMessage(), e
            );
        }
    }

    @Override
    public boolean actualizar(Asignacion entidad) {

        if (entidad == null || entidad.getIdAsignacion() == null) {
            throw new IllegalArgumentException(
                    "La asignación debe tener un ID válido."
            );
        }

        String sql = """
                UPDATE ASIGNACION
                SET id_guardia = ?,
                    id_servicio = ?,
                    id_jornada = ?,
                    fecha_asignacion = ?
                WHERE id_asignacion = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            asignarParametros(stmt, entidad);
            stmt.setInt(5, entidad.getIdAsignacion());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar la asignación: "
                            + e.getMessage(), e
            );
        }
    }

    @Override
    public boolean eliminar(Asignacion entidad) {

        if (entidad == null || entidad.getIdAsignacion() == null) {
            throw new IllegalArgumentException(
                    "La asignación debe tener un ID válido."
            );
        }

        String sql =
                "DELETE FROM ASIGNACION WHERE id_asignacion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entidad.getIdAsignacion());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al eliminar la asignación: "
                            + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<Asignacion> obtenerPorId(Integer id) {

        if (id == null || id <= 0) {
            return Optional.empty();
        }

        String sql = "SELECT " + COLUMNAS
                + " FROM ASIGNACION WHERE id_asignacion = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearAsignacion(rs));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar la asignación: "
                            + e.getMessage(), e
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Asignacion> obtenerTodos() {

        String sql = "SELECT " + COLUMNAS
                + " FROM ASIGNACION "
                + "ORDER BY fecha_asignacion DESC, id_asignacion DESC";

        return consultarLista(sql);
    }

    @Override
    public List<Asignacion> obtenerAsignacionesPorGuardia(
            Integer idGuardia) {

        if (idGuardia == null || idGuardia <= 0) {
            return new ArrayList<>();
        }

        String sql = "SELECT " + COLUMNAS
                + " FROM ASIGNACION "
                + "WHERE id_guardia = ? "
                + "ORDER BY fecha_asignacion DESC, id_asignacion DESC";

        List<Asignacion> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGuardia);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsignacion(rs));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar asignaciones por guardia: "
                            + e.getMessage(), e
            );
        }

        return lista;
    }

    private List<Asignacion> consultarLista(String sql) {

        List<Asignacion> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAsignacion(rs));
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar asignaciones: "
                            + e.getMessage(), e
            );
        }

        return lista;
    }

    private void asignarParametros(
            PreparedStatement stmt,
            Asignacion asignacion) throws SQLException {

        if (asignacion == null
                || asignacion.getIdGuardia() == null
                || asignacion.getIdServicio() == null
                || asignacion.getIdJornada() == null
                || asignacion.getFechaAsignacion() == null) {

            throw new IllegalArgumentException(
                    "Guardia, servicio, jornada y fecha son obligatorios."
            );
        }

        stmt.setInt(1, asignacion.getIdGuardia());
        stmt.setInt(2, asignacion.getIdServicio());
        stmt.setInt(3, asignacion.getIdJornada());
        stmt.setDate(
                4,
                Date.valueOf(asignacion.getFechaAsignacion())
        );
    }

    private Asignacion mapearAsignacion(ResultSet rs)
            throws SQLException {

        Date fechaSql = rs.getDate("fecha_asignacion");

        return new Asignacion(
                rs.getInt("id_asignacion"),
                rs.getInt("id_guardia"),
                rs.getInt("id_servicio"),
                rs.getInt("id_jornada"),
                fechaSql == null ? null : fechaSql.toLocalDate()
        );
    }
}
