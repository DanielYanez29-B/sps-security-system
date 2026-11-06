package com.sps.security.dao.impl;

import com.sps.security.dao.PermisoDAO;
import com.sps.security.model.access.Permiso;
import com.sps.security.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PermisoDAOImpl implements PermisoDAO {

    private static final String INSERT_PERMISO =
            "INSERT INTO PERMISO (clave_permiso, descripcion) VALUES (?, ?)";
    private static final String UPDATE_PERMISO =
            "UPDATE PERMISO SET clave_permiso = ?, descripcion = ? WHERE id_permiso = ?";
    private static final String DELETE_PERMISO =
            "DELETE FROM PERMISO WHERE id_permiso = ?";
    private static final String SELECT_PERMISO_BY_ID =
            "SELECT * FROM PERMISO WHERE id_permiso = ?";
    private static final String SELECT_ALL_PERMISOS =
            "SELECT * FROM PERMISO";
    private static final String SELECT_PERMISOS_BY_ROL_ID =
            "SELECT p.* FROM PERMISO p INNER JOIN ROL_PERMISO rp ON p.id_permiso = rp.id_permiso WHERE rp.id_rol = ?";

    @Override
    public boolean guardar(Permiso permiso) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_PERMISO, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParams(pstmt, permiso);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        permiso.setIdPermiso(rs.getInt(1));
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
    public boolean actualizar(Permiso permiso) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_PERMISO)) {

            setStatementParams(pstmt, permiso);
            pstmt.setInt(3, permiso.getIdPermiso());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminar(Permiso permiso) {
        if (permiso == null || permiso.getIdPermiso() == null) return false;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_PERMISO)) {

            pstmt.setInt(1, permiso.getIdPermiso());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Permiso> obtenerPorId(Integer id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_PERMISO_BY_ID)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearPermiso(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Permiso> obtenerTodos() {
        List<Permiso> permisos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_PERMISOS);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                permisos.add(mapearPermiso(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permisos;
    }

    @Override
    public List<Permiso> obtenerPorRolId(Integer idRol) {
        List<Permiso> permisos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_PERMISOS_BY_ROL_ID)) {

            pstmt.setInt(1, idRol);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    permisos.add(mapearPermiso(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permisos;
    }

    private void setStatementParams(PreparedStatement pstmt, Permiso permiso) throws SQLException {
        pstmt.setString(1, permiso.getClavePermiso());
        pstmt.setString(2, permiso.getDescripcion());
    }

    private Permiso mapearPermiso(ResultSet rs) throws SQLException {
        Permiso permiso = new Permiso();
        permiso.setIdPermiso(rs.getInt("id_permiso"));
        permiso.setClavePermiso(rs.getString("clave_permiso"));
        permiso.setDescripcion(rs.getString("descripcion"));
        return permiso;
    }
}