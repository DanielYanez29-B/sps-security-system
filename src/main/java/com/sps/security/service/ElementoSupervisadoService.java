package com.sps.security.service;

import com.sps.security.dao.ElementoSupervisadoDAO;
import com.sps.security.dao.impl.ElementoSupervisadoDAOImpl;
import com.sps.security.model.supervised.ElementoSupervisado;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ElementoSupervisadoService {

    private final ElementoSupervisadoDAO elementoDAO;

    public ElementoSupervisadoService() {
        this(new ElementoSupervisadoDAOImpl());
    }

    public ElementoSupervisadoService(ElementoSupervisadoDAO elementoDAO) {
        this.elementoDAO = Objects.requireNonNull(
                elementoDAO,
                "El DAO de elementos supervisados no puede ser nulo."
        );
    }

    public List<ElementoSupervisado> listarElementos() {
        return elementoDAO.obtenerTodos();
    }

    public List<ElementoSupervisado> listarElementosPorServicio(Integer idServicio) {
        if (idServicio == null || idServicio <= 0) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un ID de servicio válido."
            );
        }

        return elementoDAO.obtenerPorServicio(idServicio);
    }

    public Optional<ElementoSupervisado> buscarElementoPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un ID de elemento válido."
            );
        }

        return elementoDAO.obtenerPorId(id);
    }

    public void registrarElemento(ElementoSupervisado elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException(
                    "El elemento supervisado no puede ser nulo."
            );
        }

        if (elemento.getIdElemento() != null) {
            throw new IllegalArgumentException(
                    "Un elemento nuevo no debe tener un ID asignado."
            );
        }

        validarElemento(elemento);

        if (elemento.getEstadoActivo() == null) {
            elemento.setEstadoActivo(true);
        }

        boolean guardado = elementoDAO.guardar(elemento);

        if (!guardado) {
            throw new RuntimeException(
                    "No se pudo registrar el elemento supervisado."
            );
        }
    }

    public void actualizarElemento(ElementoSupervisado elemento) {
        if (elemento == null
                || elemento.getIdElemento() == null
                || elemento.getIdElemento() <= 0) {
            throw new IllegalArgumentException(
                    "Se requiere un ID de elemento válido para actualizar."
            );
        }

        validarElemento(elemento);

        if (elemento.getEstadoActivo() == null) {
            throw new IllegalArgumentException(
                    "Debe indicar si el elemento está activo o inactivo."
            );
        }

        boolean actualizado = elementoDAO.actualizar(elemento);

        if (!actualizado) {
            throw new RuntimeException(
                    "No se pudo actualizar el elemento supervisado."
            );
        }
    }

    public void eliminarElemento(ElementoSupervisado elemento) {
        if (elemento == null
                || elemento.getIdElemento() == null
                || elemento.getIdElemento() <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un elemento válido para eliminar."
            );
        }

        boolean eliminado = elementoDAO.eliminar(elemento);

        if (!eliminado) {
            throw new RuntimeException(
                    "No se pudo eliminar el elemento supervisado."
            );
        }
    }

    private void validarElemento(ElementoSupervisado elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException(
                    "El elemento supervisado no puede ser nulo."
            );
        }

        if (elemento.getIdServicio() == null
                || elemento.getIdServicio() <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un servicio válido."
            );
        }

        if (elemento.getTipoElemento() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar el tipo de elemento."
            );
        }

        if (elemento.getIdentificador() == null
                || elemento.getIdentificador().isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del elemento es obligatorio."
            );
        }
    }
}
