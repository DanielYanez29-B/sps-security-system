package com.sps.security.dao.impl;


import com.sps.security.dao.UsuarioDAO;
import com.sps.security.model.users.Usuario;
import com.sps.security.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {
    private static final String INSERT_USER = "INSERT INTO USUARIO (id_rol, username, email, contrasena, estado_activo) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE USUARIO SET id_rol = ?, username = ?, email = ?, contrasena = ?, estado_activo = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM USUARIO WHERE id = ?";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM USUARIO WHERE id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM USUARIO";
    private static final String AUTENTICAR = "SELECT * FROM USUARIO WHERE email = ? AND contrasena = ?";
    @Override
    public boolean guardar(Usuario usuario) {
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {


            pstmt.setInt(1, usuario.getIdRol());
            pstmt.setString(2, usuario.getUsername());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getContraseña());
            pstmt.setBoolean(5, usuario.getEstadoActivo());

            int filasAfectadas = pstmt.executeUpdate();

            if  (filasAfectadas > 0) {
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
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_USER, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, usuario.getIdRol());
            pstmt.setString(2, usuario.getUsername());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getContraseña());
            pstmt.setBoolean(5, usuario.getEstadoActivo());
            pstmt.setInt(6, usuario.getIdUsuario());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(Usuario usuario) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_USER, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, usuario.getIdUsuario());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return  false;
        }
    }

    @Override
    public Optional<Usuario> obtenerPorId(Integer id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_USER_BY_ID, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }

        }catch (SQLException e) {
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
    public Optional<Usuario> autenticar(String email, String password) {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(AUTENTICAR)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

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

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setIdRol(rs.getInt("id_rol"));
        usuario.setUsername(rs.getString("username"));
        usuario.setEmail(rs.getString("email"));
        usuario.setContraseña(rs.getString("contraseña"));
        usuario.setEstadoActivo(rs.getBoolean("estado_activo"));
        return usuario;
    }
}
