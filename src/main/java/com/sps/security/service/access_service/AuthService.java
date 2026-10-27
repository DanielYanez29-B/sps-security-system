package com.sps.security.service.access_service;

import com.sps.security.dao.impl.UsuarioDAOImpl;
import com.sps.security.model.users.Usuario;

import java.util.Optional;

public class AuthService {
    private static final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();

    public Optional<Usuario> verificarUsuario(String email, String password) {
        return usuarioDAO.autenticar(email, password);
    }
}
