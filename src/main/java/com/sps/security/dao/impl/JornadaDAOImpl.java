package com.sps.security.dao.impl;

import com.sps.security.dao.JornadaDAO;
import com.sps.security.model.Jornada;
import com.sps.security.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JornadaDAOImpl implements JornadaDAO {

    @Override
    public boolean guardar(Jornada jornada) {
        // CORRECCIÓN: nombre -> nombre_jornada | activo -> estado_actividad
        String sql = "INSERT INTO JORNADA (nombre_jornada, hora_inicio, hora_fin, estado_actividad) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jornada.getNombre());
            stmt.setTime(2, Time.valueOf(jornada.getHoraInicio()));
            stmt.setTime(3, Time.valueOf(jornada.getHoraFin()));
            stmt.setBoolean(4, jornada.isActivo());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Jornada> obtenerPorId(Integer id) {
        String sql = "SELECT * FROM JORNADA WHERE id_jornada = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearJornada(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Jornada> obtenerTodos() {
        List<Jornada> lista = new ArrayList<>();
        String sql = "SELECT * FROM JORNADA";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearJornada(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizar(Jornada jornada) {
        // CORRECCIÓN: nombre -> nombre_jornada | activo -> estado_actividad
        String sql = "UPDATE JORNADA SET nombre_jornada = ?, hora_inicio = ?, hora_fin = ?, estado_actividad = ? WHERE id_jornada = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jornada.getNombre());
            stmt.setTime(2, Time.valueOf(jornada.getHoraInicio()));
            stmt.setTime(3, Time.valueOf(jornada.getHoraFin()));
            stmt.setBoolean(4, jornada.isActivo());
            stmt.setInt(5, jornada.getIdJornada());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(Jornada jornada) {
        String sql = "DELETE FROM JORNADA WHERE id_jornada = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, jornada.getIdJornada());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Jornada> obtenerPorNombre(String nombreTurno) {
        // CORRECCIÓN: nombre -> nombre_jornada
        String sql = "SELECT * FROM JORNADA WHERE nombre_jornada = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreTurno);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearJornada(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Jornada mapearJornada(ResultSet rs) throws SQLException {
        return new Jornada(
                rs.getInt("id_jornada"),
                rs.getString("nombre_jornada"),
                rs.getTime("hora_inicio").toLocalTime(),
                rs.getTime("hora_fin").toLocalTime(),
                rs.getBoolean("estado_actividad")
        );
    }
}