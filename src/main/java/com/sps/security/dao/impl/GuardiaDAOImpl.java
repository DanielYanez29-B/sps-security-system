
package com.sps.security.dao.impl;

import com.sps.security.dao.GuardiaDAO;
import com.sps.security.model.operative.Guardia;
import com.sps.security.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class GuardiaDAOImpl implements GuardiaDAO {

    private static final String COLUMNAS =
            "id_guardia, id_usuario, nombres, apellido_paterno, "
                    + "apellido_materno, rfc, fecha_ingreso, estatus_activo";

    @Override
    public boolean guardar(Guardia entidad) {
        String sql = """
                INSERT INTO GUARDIA
                (id_usuario, nombres, apellido_paterno,
                 apellido_materno, rfc, fecha_ingreso,
                 estatus_activo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            asignarParametros(stmt, entidad);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar el guardia: " + e.getMessage(), e
            );
        }
    }

    @Override
    public boolean actualizar(Guardia entidad) {
        String sql = """
                UPDATE GUARDIA
                SET id_usuario = ?,
                    nombres = ?,
                    apellido_paterno = ?,
                    apellido_materno = ?,
                    rfc = ?,
                    fecha_ingreso = ?,
                    estatus_activo = ?
                WHERE id_guardia = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            asignarParametros(stmt, entidad);
            stmt.setInt(8, entidad.getIdGuardia());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar el guardia: " + e.getMessage(), e
            );
        }
    }

    @Override
    public boolean eliminar(Guardia entidad) {
        String sql = "DELETE FROM GUARDIA WHERE id_guardia = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entidad.getIdGuardia());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al eliminar el guardia: " + e.getMessage(), e
            );
        }
    }

    @Override
    public Optional<Guardia> obtenerPorId(Integer id) {
        String sql = "SELECT " + COLUMNAS
                + " FROM GUARDIA WHERE id_guardia = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearGuardia(rs));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar guardia por ID: "
                            + e.getMessage(), e
            );
        }

        return Optional.empty();
    }

    @Override
    public List<Guardia> obtenerTodos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM GUARDIA "
                + "ORDER BY apellido_paterno, nombres";

        return consultarLista(sql);
    }

    @Override
    public List<Guardia> obtenerGuardiasActivos() {
        String sql = "SELECT " + COLUMNAS
                + " FROM GUARDIA "
                + "WHERE estatus_activo = TRUE "
                + "ORDER BY apellido_paterno, nombres";

        return consultarLista(sql);
    }

    @Override
    public Optional<Guardia> obtenerPorRfc(String rfc) {
        if (esVacio(rfc)) {
            return Optional.empty();
        }

        String sql = "SELECT " + COLUMNAS
                + " FROM GUARDIA WHERE rfc = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, normalizarRfc(rfc));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearGuardia(rs));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar guardia por RFC: "
                            + e.getMessage(), e
            );
        }

        return Optional.empty();
    }

    @Override
    public Optional<Guardia> obtenerPorIdUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            return Optional.empty();
        }

        String sql = "SELECT " + COLUMNAS
                + " FROM GUARDIA WHERE id_usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearGuardia(rs));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar guardia por usuario: "
                            + e.getMessage(), e
            );
        }

        return Optional.empty();
    }

    private void asignarParametros(
            PreparedStatement stmt,
            Guardia guardia) throws SQLException {

        if (guardia.getIdUsuario() != null) {
            stmt.setInt(1, guardia.getIdUsuario());
        } else {
            stmt.setNull(1, Types.INTEGER);
        }

        stmt.setString(2, guardia.getNombres());
        stmt.setString(3, guardia.getApellidoPaterno());

        if (esVacio(guardia.getApellidoMaterno())) {
            stmt.setNull(4, Types.VARCHAR);
        } else {
            stmt.setString(4, guardia.getApellidoMaterno().trim());
        }

        if (esVacio(guardia.getRfc())) {
            stmt.setNull(5, Types.VARCHAR);
        } else {
            stmt.setString(5, normalizarRfc(guardia.getRfc()));
        }

        if (guardia.getFechaIngreso() != null) {
            stmt.setDate(
                    6,
                    Date.valueOf(guardia.getFechaIngreso())
            );
        } else {
            stmt.setNull(6, Types.DATE);
        }

        stmt.setBoolean(7, guardia.isEstatusActivo());
    }

    private List<Guardia> consultarLista(String sql) {
        List<Guardia> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearGuardia(rs));
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar guardias: "
                            + e.getMessage(), e
            );
        }

        return lista;
    }

    private Guardia mapearGuardia(ResultSet rs) throws SQLException {
        Integer idUsuario = rs.getObject(
                "id_usuario",
                Integer.class
        );

        Date fechaSql = rs.getDate("fecha_ingreso");

        LocalDate fechaIngreso = fechaSql != null
                ? fechaSql.toLocalDate()
                : null;

        boolean activo = rs.getBoolean("estatus_activo");

        if (rs.wasNull()) {
            activo = true;
        }

        return new Guardia(
                rs.getInt("id_guardia"),
                idUsuario,
                rs.getString("nombres"),
                rs.getString("apellido_paterno"),
                rs.getString("apellido_materno"),
                fechaIngreso,
                rs.getString("rfc"),
                activo
        );
    }

    private String normalizarRfc(String rfc) {
        if (rfc == null) {
            return null;
        }

        return rfc.trim().toUpperCase(Locale.ROOT);
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
