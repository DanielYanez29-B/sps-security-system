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
import java.sql.Types;
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

        if (!datosValidos(elemento) || elemento.getIdElemento() != null) {
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     INSERT, Statement.RETURN_GENERATED_KEYS)) {

            asignarParametros(statement, elemento);

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

        if (id == null || id <= 0) {
            return Optional.empty();
        }

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

        if (!datosValidos(elemento)
                || elemento.getIdElemento() == null
                || elemento.getIdElemento() <= 0) {
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {

            asignarParametros(statement, elemento);
            statement.setInt(6, elemento.getIdElemento());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean eliminar(ElementoSupervisado elemento) {

        if (elemento == null
                || elemento.getIdElemento() == null
                || elemento.getIdElemento() <= 0) {
            return false;
        }

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

        if (idServicio == null || idServicio <= 0) {
            return elementos;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT_BY_SERVICIO)) {

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

    private boolean datosValidos(ElementoSupervisado elemento) {

        return elemento != null
                && elemento.getIdServicio() != null
                && elemento.getIdServicio() > 0
                && elemento.getTipoElemento() != null
                && elemento.getIdentificador() != null
                && !elemento.getIdentificador().isBlank()
                && elemento.getEstadoActivo() != null;
    }

    private void asignarParametros(
            PreparedStatement statement,
            ElementoSupervisado elemento
    ) throws SQLException {

        statement.setInt(1, elemento.getIdServicio());
        statement.setString(2, elemento.getTipoElemento().name());
        statement.setString(3, elemento.getIdentificador());
        statement.setString(4, elemento.getDescripcion());

        if (elemento.getEstadoActivo() != null) {
            statement.setBoolean(5, elemento.getEstadoActivo());
        } else {
            statement.setNull(5, Types.BOOLEAN);
        }
    }

    private ElementoSupervisado mapearElemento(ResultSet result)
            throws SQLException {

        String tipo = result.getString("tipo_elemento");

        TipoElemento tipoElemento = null;

        if (tipo != null) {
            try {
                tipoElemento = TipoElemento.valueOf(tipo);
            } catch (IllegalArgumentException e) {
                throw new SQLException(
                        "Tipo de elemento no reconocido: " + tipo, e
                );
            }
        }

        boolean activo = result.getBoolean("estado_activo");
        Boolean estadoActivo = result.wasNull() ? null : activo;

        return new ElementoSupervisado(
                result.getInt("id_elemento"),
                result.getInt("id_servicio"),
                tipoElemento,
                result.getString("identificador"),
                result.getString("descripcion"),
                estadoActivo
        );
    }
}
