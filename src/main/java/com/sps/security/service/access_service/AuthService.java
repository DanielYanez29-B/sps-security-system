package com.sps.security.service.access_service;

import com.sps.security.dao.impl.PermisoDAOImpl;
import com.sps.security.dao.impl.RolDAOImpl;
import com.sps.security.dao.impl.UsuarioDAOImpl;
import com.sps.security.model.access.Permiso;
import com.sps.security.model.access.Rol;
import com.sps.security.model.users.Usuario;
import com.sps.security.util.PasswordUtil;
import com.sps.security.util.TareasBackground;

import java.util.*;
import java.util.stream.Collectors;

public class AuthService {
    private static final UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
    private static final PermisoDAOImpl permisoDAO = new PermisoDAOImpl();
    private static final RolDAOImpl rolDAO = new RolDAOImpl();

    private static final Map<Integer, Set<String>> permisosPorRol = new HashMap<>();
    private static volatile Usuario currentUser = null;
    private static Set<String> permisosActuales = Collections.emptySet();

    static {
        cargarPermisosPorRol();
    }

    public static void cargarPermisosPorRol() {   // público para poder refrescar
        permisosPorRol.clear();
        for (Rol rol : rolDAO.obtenerTodos()) {
            Set<String> claves = permisoDAO.obtenerPorRolId(rol.getIdRol())
                    .stream()
                    .map(Permiso::getClavePermiso)
                    .collect(Collectors.toSet());
            permisosPorRol.put(rol.getIdRol(), claves);
        }
    }


    public Optional<Usuario> verificarUsuario(String email, String password) {
        Optional<Usuario> usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario.isPresent()
                && Boolean.TRUE.equals(usuario.get().getEstadoActivo())
                && PasswordUtil.verificar(
                password,
                usuario.get().getContrasena())) {
            iniciarSesion(usuario.get());
            return usuario;
        }

        return Optional.empty();
    }

    private static void iniciarSesion(Usuario user) {
        currentUser = user;
        permisosActuales = permisoDAO.obtenerPorRolId(user.getIdRol())
                .stream()
                .map(Permiso::getClavePermiso)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public static void limpiarSesion() {
        currentUser = null;
        permisosActuales = Collections.emptySet();
        TareasBackground.detenerTodo();
    }

    public static boolean tienePermiso(String clavePermiso) {
        return permisosActuales.contains(clavePermiso);
    }

    public static Usuario getCurrentUser() {
        return currentUser;
    }

    public static boolean haySesion() {
        return currentUser != null;
    }
}