package com.sps.security.dao.impl;

import com.sps.security.dao.AlarmaDAO;
import com.sps.security.model.enums.EstadoAlarma;
import com.sps.security.model.enums.NivelPrioridad;
import com.sps.security.model.telemetry.Alarma;
import com.sps.security.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlarmaDAOImpl implements AlarmaDAO {
    @Override public boolean guardar(Alarma a) {
        String sql = "INSERT INTO ALARMA (id_dispositivo, fecha_hora, nivel_prioridad, tipo_alarma, estado, descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(s, a);
            if (s.executeUpdate() == 0) return false;
            try (ResultSet keys = s.getGeneratedKeys()) { if (keys.next()) a.setIdAlarma(keys.getInt(1)); }
            return true;
        } catch (SQLException e) { throw new RuntimeException("No se pudo guardar la alarma.", e); }
    }

    @Override public Optional<Alarma> obtenerPorId(Integer id) {
        return consultar("SELECT * FROM ALARMA WHERE id_alarma = ?", s -> s.setInt(1, id)).stream().findFirst();
    }

    @Override public List<Alarma> obtenerTodos() {
        return consultar("SELECT * FROM ALARMA ORDER BY fecha_hora DESC", s -> {});
    }

    @Override public List<Alarma> obtenerPorDispositivo(Integer idDispositivo) {
        return consultar("SELECT * FROM ALARMA WHERE id_dispositivo = ? ORDER BY fecha_hora DESC", s -> s.setInt(1, idDispositivo));
    }

    @Override public List<Alarma> obtenerFiltradas(LocalDateTime desde, LocalDateTime hasta, Integer idDispositivo, NivelPrioridad prioridad, EstadoAlarma estado) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ALARMA WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (desde != null) { sql.append(" AND fecha_hora >= ?"); params.add(Timestamp.valueOf(desde)); }
        if (hasta != null) { sql.append(" AND fecha_hora <= ?"); params.add(Timestamp.valueOf(hasta)); }
        if (idDispositivo != null) { sql.append(" AND id_dispositivo = ?"); params.add(idDispositivo); }
        if (prioridad != null) { sql.append(" AND nivel_prioridad = ?"); params.add(prioridad.name()); }
        if (estado != null) { sql.append(" AND estado = ?"); params.add(estado.name()); }
        sql.append(" ORDER BY fecha_hora DESC");
        return consultar(sql.toString(), s -> {
            for (int i = 0; i < params.size(); i++) s.setObject(i + 1, params.get(i));
        });
    }

    @Override public boolean actualizar(Alarma a) {
        String sql = "UPDATE ALARMA SET id_dispositivo=?, fecha_hora=?, nivel_prioridad=?, tipo_alarma=?, estado=?, descripcion=? WHERE id_alarma=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            bind(s, a); s.setInt(7, a.getIdAlarma()); return s.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("No se pudo actualizar la alarma.", e); }
    }

    @Override public boolean eliminar(Alarma a) {
        if (a == null || a.getIdAlarma() == null) return false;
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement("DELETE FROM ALARMA WHERE id_alarma=?")) {
            s.setInt(1, a.getIdAlarma()); return s.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("No se pudo eliminar la alarma.", e); }
    }

    private void bind(PreparedStatement s, Alarma a) throws SQLException {
        s.setInt(1, a.getIdDispositivo()); s.setTimestamp(2, Timestamp.valueOf(a.getFechaHora()));
        s.setString(3, a.getNivelPrioridad().name()); s.setString(4, a.getTipoAlarma());
        s.setString(5, a.getEstado().name()); s.setString(6, a.getDescripcion());
    }

    private List<Alarma> consultar(String sql, SQLBinder binder) {
        List<Alarma> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            binder.bind(s);
            try (ResultSet rs = s.executeQuery()) { while (rs.next()) result.add(mapear(rs)); }
            return result;
        } catch (SQLException e) { throw new RuntimeException("No se pudieron consultar las alarmas.", e); }
    }

    private Alarma mapear(ResultSet rs) throws SQLException {
        return new Alarma(rs.getInt("id_alarma"), rs.getInt("id_dispositivo"),
                rs.getTimestamp("fecha_hora").toLocalDateTime(),
                NivelPrioridad.valueOf(rs.getString("nivel_prioridad")),
                rs.getString("tipo_alarma"), EstadoAlarma.valueOf(rs.getString("estado")),
                rs.getString("descripcion"));
    }
    @FunctionalInterface private interface SQLBinder { void bind(PreparedStatement statement) throws SQLException; }
}
