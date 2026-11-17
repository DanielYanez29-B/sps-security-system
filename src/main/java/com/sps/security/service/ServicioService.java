
package com.sps.security.service;

import com.sps.security.dao.ServicioDAO;
import com.sps.security.dao.impl.ServicioDAOImpl;
import com.sps.security.model.operative.Servicio;

import java.util.List;
import java.util.Optional;

public class ServicioService {

    private final ServicioDAO servicioDAO;

    public ServicioService() {
        this.servicioDAO = new ServicioDAOImpl();
    }

    public ServicioService(ServicioDAO servicioDAO) {
        this.servicioDAO = servicioDAO;
    }

    public List<Servicio> listarServicios() {
        return servicioDAO.obtenerTodos();
    }

    public List<Servicio> listarServiciosActivos() {
        return servicioDAO.obtenerServiciosActivos();
    }

    public Optional<Servicio> buscarServicioPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "Debe proporcionar el ID del servicio."
            );
        }

        return servicioDAO.obtenerPorId(id);
    }

    public void registrarServicio(Servicio servicio) {
        validarServicio(servicio);

        if (servicio.getEstado() == null
                || servicio.getEstado().isBlank()) {
            servicio.setEstado("ACTIVO");
        }

        boolean guardado = servicioDAO.guardar(servicio);

        if (!guardado) {
            throw new RuntimeException(
                    "No se pudo registrar el servicio."
            );
        }
    }

    public void actualizarServicio(Servicio servicio) {
        if (servicio == null || servicio.getIdServicio() == null) {
            throw new IllegalArgumentException(
                    "Se requiere el ID del servicio para actualizar."
            );
        }

        validarServicio(servicio);

        boolean actualizado = servicioDAO.actualizar(servicio);

        if (!actualizado) {
            throw new RuntimeException(
                    "No se pudo actualizar el servicio."
            );
        }
    }

    public void eliminarServicio(Servicio servicio) {
        if (servicio == null || servicio.getIdServicio() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un servicio válido para eliminar."
            );
        }

        boolean eliminado = servicioDAO.eliminar(servicio);

        if (!eliminado) {
            throw new RuntimeException(
                    "No se pudo eliminar el servicio."
            );
        }
    }

    private void validarServicio(Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException(
                    "El servicio no puede ser nulo."
            );
        }

        if (servicio.getIdCliente() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un cliente."
            );
        }

        if (servicio.getNombreServicio() == null
                || servicio.getNombreServicio().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del servicio es obligatorio."
            );
        }

        if (servicio.getDireccion() == null
                || servicio.getDireccion().isBlank()) {
            throw new IllegalArgumentException(
                    "La dirección del servicio es obligatoria."
            );
        }

        if (servicio.getFechaInicio() == null) {
            throw new IllegalArgumentException(
                    "La fecha de inicio es obligatoria."
            );
        }
    }
}
