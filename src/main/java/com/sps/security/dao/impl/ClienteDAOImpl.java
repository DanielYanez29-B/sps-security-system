package com.sps.security.dao.impl;

import com.sps.security.dao.ClienteDAO;
import com.sps.security.model.users.Cliente;
import com.sps.security.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAOImpl implements ClienteDAO {

    private static final String INSERT_CLIENTE =
            "INSERT INTO CLIENTE " +
                    "(razon_social, rfc, telefono, email, calle, " +
                    "num_ext, colonia, ciudad, estado, cp) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_CLIENTE_BY_ID =
            "SELECT * FROM CLIENTE WHERE id_cliente = ?";

    private static final String SELECT_ALL_CLIENTES =
            "SELECT * FROM CLIENTE";

    private static final String UPDATE_CLIENTE =
            "UPDATE CLIENTE SET razon_social = ?, rfc = ?, " +
                    "telefono = ?, email = ?, calle = ?, num_ext = ?, " +
                    "colonia = ?, ciudad = ?, estado = ?, cp = ? " +
                    "WHERE id_cliente = ?";

    private static final String DELETE_CLIENTE =
            "DELETE FROM CLIENTE WHERE id_cliente = ?";

    private static final String SELECT_CLIENTE_BY_EMAIL =
            "SELECT * FROM CLIENTE WHERE email = ?";

    @Override
    public boolean guardar(Cliente entidad) {

        if (entidad == null) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     INSERT_CLIENTE,
                     Statement.RETURN_GENERATED_KEYS)) {

            asignarParametros(pstmt, entidad);

            int filas = pstmt.executeUpdate();

            if (filas > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        entidad.setIdcliente(keys.getInt(1));
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
    public Optional<Cliente> obtenerPorId(Integer id) {

        if (id == null) {
            return Optional.empty();
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(SELECT_CLIENTE_BY_ID)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<Cliente> obtenerTodos() {

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(SELECT_ALL_CLIENTES);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientes;
    }

    @Override
    public boolean actualizar(Cliente entidad) {

        if (entidad == null || entidad.getIdcliente() == null) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(UPDATE_CLIENTE)) {

            asignarParametros(pstmt, entidad);
            pstmt.setInt(11, entidad.getIdcliente());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(Cliente entidad) {

        if (entidad == null || entidad.getIdcliente() == null) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(DELETE_CLIENTE)) {

            pstmt.setInt(1, entidad.getIdcliente());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {

        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(SELECT_CLIENTE_BY_EMAIL)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private void asignarParametros(
            PreparedStatement pstmt,
            Cliente cliente) throws SQLException {

        pstmt.setString(1, cliente.getNombre());
        pstmt.setString(2, cliente.getRfc());
        pstmt.setString(3, cliente.getTelefono());
        pstmt.setString(4, cliente.getEmail());
        pstmt.setString(5, cliente.getCalle());
        pstmt.setString(6, cliente.getNum_ext());
        pstmt.setString(7, cliente.getColonia());
        pstmt.setString(8, cliente.getCiudad());
        pstmt.setString(9, cliente.getEstado());
        pstmt.setString(10, cliente.getCp());
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {

        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("razon_social"),
                rs.getString("rfc"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("calle"),
                rs.getString("num_ext"),
                rs.getString("colonia"),
                rs.getString("ciudad"),
                rs.getString("estado"),
                rs.getString("cp")
        );
    }
}
