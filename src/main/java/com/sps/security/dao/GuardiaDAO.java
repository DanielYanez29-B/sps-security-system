package com.sps.security.dao;

import com.sps.security.model.operative.Guardia;
import com.sps.security.model.users.Usuario;

import java.util.List;

public interface GuardiaDAO extends CrudDAO<Guardia, Integer>{
    List<Guardia> obtenerGuardiasActivos();
}
