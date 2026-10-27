package com.sps.security.dao;

import com.sps.security.model.users.Usuario;

import java.util.Optional;

public interface UsuarioDAO extends CrudDAO<Usuario, Integer>{
    Optional<Usuario> autenticar(String email, String password);
}
