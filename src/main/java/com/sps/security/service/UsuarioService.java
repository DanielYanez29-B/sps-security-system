package com.sps.security.service;

import com.sps.security.dao.UsuarioDAO;
import com.sps.security.dao.impl.UsuarioDAOImpl;
import com.sps.security.model.users.Usuario;
import com.sps.security.util.PasswordUtil;

import java.util.List;

public class  UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.obtenerTodos();
    }

    public void registrarUsuario(Usuario usuario) {
        validarCamposObligatorios(usuario, true);
        validarFormatoEmail(usuario.getEmail());

        usuario.setContrasena(
                PasswordUtil.hash(usuario.getContrasena())
        );

        boolean guardado = usuarioDAO.guardar(usuario);
        if (!guardado) {
            throw new RuntimeException("Error en base de datos: No se pudo registrar el usuario.");
        }
    }


    public void actualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("Se requiere el ID del usuario para actualizar.");
        }
        validarCamposObligatorios(usuario, false);
        validarFormatoEmail(usuario.getEmail());
        String contrasena =
                prepararContrasenaParaActualizar(
                        usuario.getContrasena()
                );

        if (contrasena != null) {
            usuario.setContrasena(contrasena);
        }
        boolean actualizado = usuarioDAO.actualizar(usuario);
        if (!actualizado) {
            throw new RuntimeException("Error en base de datos: No se pudo actualizar el registro.");
        }
    }

    public void eliminarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new IllegalArgumentException("Debe seleccionar un usuario válido para eliminar.");
        }

        boolean eliminado = usuarioDAO.eliminar(usuario);
        if (!eliminado) {
            throw new RuntimeException("Error en base de datos: No se pudo eliminar el usuario.");
        }
    }

    // Reglas de Validación de Negocio
    private void validarCamposObligatorios(Usuario u, boolean esNuevo) {
        if (u == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (u.getUsername() == null || u.getUsername().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario (username) es obligatorio.");
        }
        if (u.getEmail() == null || u.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (u.getIdRol() == null || u.getIdRol() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un rol válido.");
        }
        if (esNuevo && (u.getContrasena() == null || u.getContrasena().isBlank())) {
            throw new IllegalArgumentException("La contraseña es obligatoria para nuevos registros.");
        }
    }

    private void validarFormatoEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(regex)) {
            throw new IllegalArgumentException("El formato del correo electrónico es inválido.");
        }

    }

    private String prepararContrasenaParaActualizar(
            String contrasena
    ) {
        if (contrasena == null || contrasena.isBlank()) {
            return null;
        }

        if (contrasena.startsWith("$2")) {
            return contrasena;
        }

        return PasswordUtil.hash(contrasena);
    }
}
