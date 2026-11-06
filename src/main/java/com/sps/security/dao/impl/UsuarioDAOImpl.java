package com.sps.security.dao.impl;

import com.sps.security.dao.UsuarioDAO;
import com.sps.security.model.users.Usuario;
import com.sps.security.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String INSERT_USER =
            "INSERT INTO USUARIO (id_rol, username, email, contrasena, estado_activo) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER =
            "UPDATE USUARIO SET " +
                    "id_rol = ?, username = ?, email = ?, " +
                    "estado_activo = ? " +
                    "WHERE id_usuario = ?";
    private static final String DELETE_USER =
            "DELETE FROM USUARIO WHERE id_usuario = ?";
    private static final String SELECT_USER_BY_ID =
            "SELECT * FROM USUARIO WHERE id_usuario = ?";
    private static final String SELECT_ALL_USERS =
            "SELECT * FROM USUARIO";
    private static final String AUTENTICAR =
            "SELECT * FROM USUARIO WHERE email = ?";

    @Override
    public boolean guardar(Usuario usuario) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParams(pstmt, usuario);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setIdUsuario(rs.getInt(1));
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
    public boolean actualizar(Usuario usuario) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_USER)) {

            setStatementParams(pstmt, usuario);
            pstmt.setInt(6, usuario.getIdUsuario());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) return false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_USER)) {

            pstmt.setInt(1, usuario.getIdUsuario());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Usuario> obtenerPorId(Integer id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_USER_BY_ID)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt =
                     conn.prepareStatement(AUTENTICAR)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo buscar el usuario por email.",
                    e
            );
        }

        return Optional.empty();
    }

    // Helper para evitar duplicar código de binding en INSERT y UPDATE
    private void setStatementParams(PreparedStatement pstmt, Usuario usuario) throws SQLException {
        if (usuario.getIdRol() != null) {
            pstmt.setInt(1, usuario.getIdRol());
        } else {
            pstmt.setNull(1, Types.INTEGER);
        }
        pstmt.setString(2, usuario.getUsername());
        pstmt.setString(3, usuario.getEmail());
        pstmt.setString(4, usuario.getContrasena());
        pstmt.setBoolean(5, usuario.getEstadoActivo() != null && usuario.getEstadoActivo());
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setIdRol(rs.getInt("id_rol"));
        usuario.setUsername(rs.getString("username"));
        usuario.setEmail(rs.getString("email"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setEstadoActivo(rs.getBoolean("estado_activo"));
        return usuario;
    }

}