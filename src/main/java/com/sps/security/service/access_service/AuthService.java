package com.sps.security.service.access_service;

import com.sps.security.dao.UsuarioDAO;
import com.sps.security.dao.impl.UsuarioDAOImpl;
import com.sps.security.model.users.Usuario;
import com.sps.security.util.PasswordUtil;

import java.util.Optional;

public class AuthService {

    private final UsuarioDAO usuarioDAO;
    private static Usuario currentUser;

    public AuthService() {
        this.usuarioDAO = new UsuarioDAOImpl();
    }

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Optional<Usuario> verificarUsuario(
            String email,
            String password
    ) {
        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            return Optional.empty();
        }

        Optional<Usuario> resultado =
                usuarioDAO.buscarPorEmail(email.trim());

        if (resultado.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = resultado.get();

        if (!Boolean.TRUE.equals(usuario.getEstadoActivo())) {
            return Optional.empty();
        }

        if (!PasswordUtil.verificar(
                password,
                usuario.getContrasena())) {
            return Optional.empty();
        }

        return Optional.of(usuario);
    }

    public static void setCurrentUser(Usuario user) {
        currentUser = user;
    }

    public static Usuario getCurrentUser() {
        return currentUser;
    }

    public static void limpiarSesion() {
        currentUser = null;
    }
}