package com.sps.security.service.access_service;

import com.sps.security.dao.impl.UsuarioDAOImpl;
import com.sps.security.model.users.Usuario;

import java.util.Optional;

public class AuthService {
    private static final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
    private static Usuario currentUser = new Usuario();


    public Optional<Usuario> verificarUsuario(String email, String password) {
        Optional<Usuario> usuario = usuarioDAO.autenticar(email, password);
        if (usuario.isPresent()) {
            if (usuario.get().getEstadoActivo()) {
                setCurrentUser(usuario.get());
                return usuario;
            }
        }
        return Optional.empty();
    }

    private static void setCurrentUser(Usuario user) {
        currentUser = user;
    }
    public static Usuario getCurrentUser() {
        return currentUser;
    }
}