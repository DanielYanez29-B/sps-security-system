package com.sps.security.dao;

import com.sps.security.model.users.Cliente;

import java.util.Optional;

public interface ClienteDAO extends CrudDAO<Cliente, Integer> {
    Optional<Cliente> buscarPorEmail(String email);

}
