package com.sps.security.dao.impl;

import com.sps.security.dao.RolDAO;
import com.sps.security.model.access.Rol;
import com.sps.security.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RolDAOImpl implements RolDAO {

    private static final String INSERT_ROL =
            "INSERT INTO ROL (nombre, descripcion) VALUES (?, ?)";
    private static final String UPDATE_ROL =
            "UPDATE ROL SET nombre = ?, descripcion = ? WHERE id_rol = ?";
    private static final String DELETE_ROL =
            "DELETE FROM ROL WHERE id_rol = ?";
    private static final String SELECT_ROL_BY_ID =
            "SELECT * FROM ROL WHERE id_rol = ?";
    private static final String SELECT_ALL_ROLES =
            "SELECT * FROM ROL";
    private static final String SELECT_ROL_BY_NOMBRE =
            "SELECT * FROM ROL WHERE nombre = ?";

    // Atributos para manejar la relacion N:M de roles y permisos
    private static final String INSERT_ROL_PERMISO =
            "INSERT INTO ROL_PERMISO (id_rol, id_permiso) VALUES (?, ?)";
    private static final String DELETE_ROL_PERMISO =
            "DELETE FROM ROL_PERMISO WHERE id_rol = ? AND id_permiso = ?";
    private static final String DELETE_ALL_PERMISOS_BY_ROL =
            "DELETE FROM ROL_PERMISO WHERE id_rol = ?";

    @Override
    public boolean guardar(Rol rol) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_ROL, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParams(pstmt, rol);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        rol.setIdRol(rs.getInt(1));
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
    public boolean actualizar(Rol rol) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_ROL)) {

            setStatementParams(pstmt, rol);
            pstmt.setInt(3, rol.getIdRol());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(Rol rol) {
        if (rol == null || rol.getIdRol() == null) return false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_ROL)) {

            pstmt.setInt(1, rol.getIdRol());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Rol> obtenerPorId(Integer id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ROL_BY_ID)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearRol(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Rol> obtenerTodos() {
        List<Rol> roles = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_ROLES);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                roles.add(mapearRol(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public Optional<Rol> buscarPorNombre(String nombre) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ROL_BY_NOMBRE)) {

            pstmt.setString(1, nombre);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearRol(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Metodos para manejar la relacion N:M de roles y permisos
    @Override
    public boolean asignarPermiso(Integer idRol, Integer idPermiso) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_ROL_PERMISO)) {

            pstmt.setInt(1, idRol);
            pstmt.setInt(2, idPermiso);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean revocarPermiso(Integer idRol, Integer idPermiso) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_ROL_PERMISO)) {

            pstmt.setInt(1, idRol);
            pstmt.setInt(2, idPermiso);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean limpiarPermisos(Integer idRol) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_ALL_PERMISOS_BY_ROL)) {

            pstmt.setInt(1, idRol);
            return pstmt.executeUpdate() >= 0; // >= 0 porque puede no tener permisos que borrar

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setStatementParams(PreparedStatement pstmt, Rol rol) throws SQLException {
        pstmt.setString(1, rol.getNombre());
        pstmt.setString(2, rol.getDescripcion());
    }

    private Rol mapearRol(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));
        rol.setNombre(rs.getString("nombre"));
        rol.setDescripcion(rs.getString("descripcion"));
        return rol;
    }
}