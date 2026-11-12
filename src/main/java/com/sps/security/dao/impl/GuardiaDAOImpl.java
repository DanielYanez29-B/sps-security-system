package com.sps.security.dao.impl;

import com.sps.security.dao.GuardiaDAO;
import com.sps.security.model.operative.Guardia;
import com.sps.security.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuardiaDAOImpl implements GuardiaDAO {

    @Override
    public boolean guardar(Guardia entidad) {

        String sql = """
                INSERT INTO GUARDIA
                (id_usuario, nombres, apellido_paterno, apellido_materno,
                 fecha_ingreso, estatus_activo)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (entidad.getIdUsuario() != null) {
                stmt.setInt(1, entidad.getIdUsuario());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setString(2, entidad.getNombres());
            stmt.setString(3, entidad.getApellidoPaterno());
            stmt.setString(4, entidad.getApellidoMaterno());

            if (entidad.getFechaIngreso() != null) {
                stmt.setDate(5, java.sql.Date.valueOf(entidad.getFechaIngreso()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            if (entidad.getEstatusActivo() != null) {
                stmt.setBoolean(6, entidad.getEstatusActivo());
            } else {
                stmt.setNull(6, Types.BOOLEAN);
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar el guardia:");
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean actualizar(Guardia entidad) {

        String sql = """
                UPDATE GUARDIA
                SET id_usuario = ?,
                    nombres = ?,
                    apellido_paterno = ?,
                    apellido_materno = ?,
                    fecha_ingreso = ?,
                    estatus_activo = ?
                WHERE id_guardia = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (entidad.getIdUsuario() != null) {
                stmt.setInt(1, entidad.getIdUsuario());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setString(2, entidad.getNombres());
            stmt.setString(3, entidad.getApellidoPaterno());
            stmt.setString(4, entidad.getApellidoMaterno());

            if (entidad.getFechaIngreso() != null) {
                stmt.setDate(5, java.sql.Date.valueOf(entidad.getFechaIngreso()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            if (entidad.getEstatusActivo() != null) {
                stmt.setBoolean(6, entidad.getEstatusActivo());
            } else {
                stmt.setNull(6, Types.BOOLEAN);
            }

            stmt.setInt(7, entidad.getIdGuardia());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar el guardia:");
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean eliminar(Guardia entidad) {

        String sql = "DELETE FROM GUARDIA WHERE id_guardia = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entidad.getIdGuardia());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar el guardia:");
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Optional<Guardia> obtenerPorId(Integer id) {

        String sql = "SELECT * FROM GUARDIA WHERE id_guardia = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearGuardia(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el guardia:");
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Guardia> obtenerTodos() {

        List<Guardia> lista = new ArrayList<>();

        String sql = "SELECT * FROM GUARDIA";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearGuardia(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los guardias:");
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public List<Guardia> obtenerGuardiasActivos() {

        List<Guardia> lista = new ArrayList<>();

        String sql = "SELECT * FROM GUARDIA WHERE estatus_activo = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearGuardia(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los guardias activos:");
            e.printStackTrace();
        }

        return lista;
    }

    private Guardia mapearGuardia(ResultSet rs) throws SQLException {

        Integer idGuardia = (Integer) rs.getObject("id_guardia");
        Integer idUsuario = (Integer) rs.getObject("id_usuario");

        java.sql.Date sqlDate = rs.getDate("fecha_ingreso");
        LocalDate fechaIngreso = sqlDate != null
                ? sqlDate.toLocalDate()
                : null;

        boolean estatus = rs.getBoolean("estatus_activo");
        Boolean estatusActivo = rs.wasNull() ? null : estatus;

        return new Guardia(
                idGuardia,
                null,
                idUsuario,
                rs.getString("nombres"),
                rs.getString("apellido_paterno"),
                rs.getString("apellido_materno"),
                fechaIngreso,
                estatusActivo
        );
    }
}