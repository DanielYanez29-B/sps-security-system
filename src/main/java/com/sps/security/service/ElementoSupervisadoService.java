
package com.sps.security.service;

import com.sps.security.dao.ElementoSupervisadoDAO;
import com.sps.security.dao.impl.ElementoSupervisadoDAOImpl;
import com.sps.security.model.supervised.ElementoSupervisado;

import java.util.List;
import java.util.Optional;

public class ElementoSupervisadoService {

    private final ElementoSupervisadoDAO elementoDAO;

    public ElementoSupervisadoService() {
        this.elementoDAO = new ElementoSupervisadoDAOImpl();
    }

    public ElementoSupervisadoService(ElementoSupervisadoDAO elementoDAO) {
        this.elementoDAO = elementoDAO;
    }

    public List<ElementoSupervisado> listarElementos() {
        return elementoDAO.obtenerTodos();
    }

    public List<ElementoSupervisado> listarElementosPorServicio(Integer idServicio) {
        if (idServicio == null) {
            throw new IllegalArgumentException(
                    "Debe proporcionar el ID del servicio."
            );
        }

        return elementoDAO.obtenerPorServicio(idServicio);
    }

    public Optional<ElementoSupervisado> buscarElementoPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "Debe proporcionar el ID del elemento."
            );
        }

        return elementoDAO.obtenerPorId(id);
    }

    public void registrarElemento(ElementoSupervisado elemento) {
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
        if (elemento == null || elemento.getIdElemento() == null) {
            throw new IllegalArgumentException(
                    "Se requiere el ID del elemento para actualizar."
            );
        }

        validarElemento(elemento);

        boolean actualizado = elementoDAO.actualizar(elemento);

        if (!actualizado) {
            throw new RuntimeException(
                    "No se pudo actualizar el elemento supervisado."
            );
        }
    }

    public void eliminarElemento(ElementoSupervisado elemento) {
        if (elemento == null || elemento.getIdElemento() == null) {
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

        if (elemento.getIdServicio() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un servicio."
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
