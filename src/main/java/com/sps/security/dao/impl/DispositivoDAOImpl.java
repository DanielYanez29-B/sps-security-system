package com.sps.security.dao.impl;

import com.sps.security.dao.DispositivoDAO;
import com.sps.security.model.telemetry.Dispositivo;
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

public class DispositivoDAOImpl implements DispositivoDAO {

    private static final String INSERT =
            "INSERT INTO DISPOSITIVO " +
                    "(id_servicio, num_serie, mac_address, ip_asignada, " +
                    "fabricante, fecha_instalacion, estado_actividad) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE DISPOSITIVO SET " +
                    "id_servicio = ?, " +
                    "num_serie = ?, " +
                    "mac_address = ?, " +
                    "ip_asignada = ?, " +
                    "fabricante = ?, " +
                    "fecha_instalacion = ?, " +
                    "estado_actividad = ? " +
                    "WHERE id_dispositivo = ?";

    private static final String DELETE =
            "DELETE FROM DISPOSITIVO WHERE id_dispositivo = ?";

    private static final String FIND_BY_ID =
            "SELECT * FROM DISPOSITIVO WHERE id_dispositivo = ?";

    private static final String FIND_ALL =
            "SELECT * FROM DISPOSITIVO ORDER BY id_dispositivo";

    private static final String FIND_BY_SERIAL =
            "SELECT * FROM DISPOSITIVO WHERE num_serie = ?";

    @Override
    public boolean guardar(Dispositivo dispositivo) {
        String sql = INSERT;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, dispositivo.getIdServicio());
            statement.setString(2, dispositivo.getNumSerie());

            establecerTextoOpcional(
                    statement,
                    3,
                    dispositivo.getMacAddress()
            );

            establecerTextoOpcional(
                    statement,
                    4,
                    dispositivo.getIpAsignada()
            );

            statement.setString(5, dispositivo.getFabricante());

            if (dispositivo.getFechaInstalacion() == null) {
                statement.setNull(6, Types.DATE);
            } else {
                statement.setDate(
                        6,
                        java.sql.Date.valueOf(
                                dispositivo.getFechaInstalacion()
                        )
                );
            }

            statement.setBoolean(
                    7,
                    dispositivo.getEstadoActividad() != null
                            && dispositivo.getEstadoActividad()
            );

            int filasAfectadas = statement.executeUpdate();

            if (filasAfectadas == 0) {
                return false;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    dispositivo.setIdDispositivo(keys.getInt(1));
                }
            }

            return true;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo guardar el dispositivo.",
                    exception
            );
        }
    }

    @Override
    public Optional<Dispositivo> obtenerPorId(Integer id) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_BY_ID)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapear(resultSet));
                }
            }

            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo consultar el dispositivo.",
                    exception
            );
        }
    }

    @Override
    public List<Dispositivo> obtenerTodos() {
        List<Dispositivo> dispositivos = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                dispositivos.add(mapear(resultSet));
            }

            return dispositivos;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudieron consultar los dispositivos.",
                    exception
            );
        }
    }

    @Override
    public boolean actualizar(Dispositivo dispositivo) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(UPDATE)) {

            statement.setInt(1, dispositivo.getIdServicio());
            statement.setString(2, dispositivo.getNumSerie());

            establecerTextoOpcional(
                    statement,
                    3,
                    dispositivo.getMacAddress()
            );

            establecerTextoOpcional(
                    statement,
                    4,
                    dispositivo.getIpAsignada()
            );

            statement.setString(5, dispositivo.getFabricante());

            if (dispositivo.getFechaInstalacion() == null) {
                statement.setNull(6, Types.DATE);
            } else {
                statement.setDate(
                        6,
                        java.sql.Date.valueOf(
                                dispositivo.getFechaInstalacion()
                        )
                );
            }

            statement.setBoolean(
                    7,
                    dispositivo.getEstadoActividad() != null
                            && dispositivo.getEstadoActividad()
            );

            statement.setInt(8, dispositivo.getIdDispositivo());

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo actualizar el dispositivo.",
                    exception
            );
        }
    }

    @Override
    public boolean eliminar(Dispositivo dispositivo) {
        if (dispositivo == null
                || dispositivo.getIdDispositivo() == null) {
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(DELETE)) {

            statement.setInt(1, dispositivo.getIdDispositivo());

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo eliminar el dispositivo.",
                    exception
            );
        }
    }

    @Override
    public Optional<Dispositivo> obtenerPorNumSerie(String numSerie) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_BY_SERIAL)) {

            statement.setString(1, numSerie);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapear(resultSet));
                }
            }

            return Optional.empty();

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo consultar el número de serie.",
                    exception
            );
        }
    }

    private Dispositivo mapear(ResultSet resultSet) throws SQLException {
        java.sql.Date fechaSql =
                resultSet.getDate("fecha_instalacion");

        LocalDate fechaInstalacion =
                fechaSql == null ? null : fechaSql.toLocalDate();

        return new Dispositivo(
                resultSet.getInt("id_dispositivo"),
                resultSet.getInt("id_servicio"),
                resultSet.getString("num_serie"),
                resultSet.getString("mac_address"),
                resultSet.getString("ip_asignada"),
                resultSet.getString("fabricante"),
                fechaInstalacion,
                resultSet.getBoolean("estado_actividad")
        );
    }

    private void establecerTextoOpcional(
            PreparedStatement statement,
            int posicion,
            String valor
    ) throws SQLException {
        if (valor == null || valor.isBlank()) {
            statement.setNull(posicion, Types.VARCHAR);
        } else {
            statement.setString(posicion, valor);
        }
    }
}