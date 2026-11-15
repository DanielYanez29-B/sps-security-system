
package com.sps.security.dao.impl;

import com.sps.security.dao.ElementoSupervisadoDAO;
import com.sps.security.model.enums.TipoElemento;
import com.sps.security.model.supervised.ElementoSupervisado;
import com.sps.security.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ElementoSupervisadoDAOImpl implements ElementoSupervisadoDAO {

    private static final String INSERT =
            "INSERT INTO ELEMENTO_SUPERVISADO " +
                    "(id_servicio, tipo_elemento, identificador, descripcion, estado_activo) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID =
            "SELECT * FROM ELEMENTO_SUPERVISADO WHERE id_elemento = ?";

    private static final String SELECT_ALL =
            "SELECT * FROM ELEMENTO_SUPERVISADO";

    private static final String UPDATE =
            "UPDATE ELEMENTO_SUPERVISADO SET " +
                    "id_servicio = ?, tipo_elemento = ?, identificador = ?, " +
                    "descripcion = ?, estado_activo = ? " +
                    "WHERE id_elemento = ?";

    private static final String DELETE =
            "DELETE FROM ELEMENTO_SUPERVISADO WHERE id_elemento = ?";

    private static final String SELECT_BY_SERVICIO =
            "SELECT * FROM ELEMENTO_SUPERVISADO WHERE id_servicio = ?";

    @Override
    public boolean guardar(ElementoSupervisado elemento) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, elemento.getIdServicio());
            statement.setString(2, elemento.getTipoElemento().name());
            statement.setString(3, elemento.getIdentificador());
            statement.setString(4, elemento.getDescripcion());
            statement.setBoolean(5, elemento.getEstadoActivo());

            int filas = statement.executeUpdate();

            if (filas > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        elemento.setIdElemento(keys.getInt(1));
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
    public Optional<ElementoSupervisado> obtenerPorId(Integer id) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapearElemento(result));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<ElementoSupervisado> obtenerTodos() {
        List<ElementoSupervisado> elementos = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                elementos.add(mapearElemento(result));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return elementos;
    }

    @Override
    public boolean actualizar(ElementoSupervisado elemento) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {

            statement.setInt(1, elemento.getIdServicio());
            statement.setString(2, elemento.getTipoElemento().name());
            statement.setString(3, elemento.getIdentificador());
            statement.setString(4, elemento.getDescripcion());
            statement.setBoolean(5, elemento.getEstadoActivo());
            statement.setInt(6, elemento.getIdElemento());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean eliminar(ElementoSupervisado elemento) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {

            statement.setInt(1, elemento.getIdElemento());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<ElementoSupervisado> obtenerPorServicio(Integer idServicio) {
        List<ElementoSupervisado> elementos = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_SERVICIO)) {

            statement.setInt(1, idServicio);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    elementos.add(mapearElemento(result));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return elementos;
    }

    private ElementoSupervisado mapearElemento(ResultSet result) throws SQLException {
        return new ElementoSupervisado(
                result.getInt("id_elemento"),
                result.getInt("id_servicio"),
                TipoElemento.valueOf(result.getString("tipo_elemento")),
                result.getString("identificador"),
                result.getString("descripcion"),
                result.getBoolean("estado_activo")
        );
    }
}
