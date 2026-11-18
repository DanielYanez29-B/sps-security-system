package com.sps.security.dao.impl;

import com.sps.security.dao.ServicioDAO;
import com.sps.security.model.operative.Servicio;
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

public class ServicioDAOImpl implements ServicioDAO {

    private static final String INSERT =
            "INSERT INTO SERVICIO " +
                    "(id_cliente, nombre_servicio, direccion, fecha_inicio, estado) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID =
            "SELECT * FROM SERVICIO WHERE id_servicio = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM SERVICIO";

    private static final String UPDATE =
            "UPDATE SERVICIO SET " +
                    "id_cliente = ?, nombre_servicio = ?, direccion = ?, " +
                    "fecha_inicio = ?, estado = ? " +
                    "WHERE id_servicio = ?";

    private static final String DELETE =
            "DELETE FROM SERVICIO WHERE id_servicio = ?";

    private static final String SELECT_SERVICIOS_ACTIVOS =
            "SELECT * FROM SERVICIO WHERE estado = 'ACTIVO'";

    @Override
    public boolean guardar(Servicio servicio) {

        if (!datosValidos(servicio)
                || servicio.getIdServicio() != null) {
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT, Statement.RETURN_GENERATED_KEYS)) {

            asignarParametros(statement, servicio);

            int filas = statement.executeUpdate();

            if (filas > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        servicio.setIdServicio(keys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Optional<Servicio> obtenerPorId(Integer id) {

        if (id == null) {
            return Optional.empty();
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_BY_ID)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapearServicio(result));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Servicio> obtenerTodos() {

        List<Servicio> servicios = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_ALL);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                servicios.add(mapearServicio(result));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return servicios;
    }

    @Override
    public boolean actualizar(Servicio servicio) {

        if (!datosValidos(servicio)
                || servicio.getIdServicio() == null) {
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(UPDATE)) {

            asignarParametros(statement, servicio);
            statement.setInt(6, servicio.getIdServicio());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean eliminar(Servicio servicio) {

        if (servicio == null
                || servicio.getIdServicio() == null) {
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(DELETE)) {

            statement.setInt(1, servicio.getIdServicio());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Servicio> obtenerServiciosActivos() {

        List<Servicio> servicios = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_SERVICIOS_ACTIVOS);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                servicios.add(mapearServicio(result));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return servicios;
    }

    private boolean datosValidos(Servicio servicio) {

        return servicio != null
                && servicio.getIdCliente() != null
                && servicio.getNombreServicio() != null
                && !servicio.getNombreServicio().isBlank()
                && servicio.getFechaInicio() != null;
    }

    private void asignarParametros(
            PreparedStatement statement,
            Servicio servicio) throws SQLException {

        statement.setInt(1, servicio.getIdCliente());
        statement.setString(2, servicio.getNombreServicio());
        statement.setString(3, servicio.getDireccion());
        statement.setDate(
                4,
                Date.valueOf(servicio.getFechaInicio())
        );
        statement.setString(5, servicio.getEstado());
    }

    private Servicio mapearServicio(
            ResultSet result) throws SQLException {

        Date fecha = result.getDate("fecha_inicio");

        return new Servicio(
                result.getInt("id_servicio"),
                result.getInt("id_cliente"),
                result.getString("nombre_servicio"),
                result.getString("direccion"),
                fecha != null ? fecha.toLocalDate() : null,
                result.getString("estado")
        );
    }
}
