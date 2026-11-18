package com.sps.security.service;

import com.sps.security.dao.ServicioDAO;
import com.sps.security.dao.impl.ServicioDAOImpl;
import com.sps.security.model.operative.Servicio;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ServicioService {

    private final ServicioDAO servicioDAO;

    public ServicioService() {
        this(new ServicioDAOImpl());
    }

    public ServicioService(ServicioDAO servicioDAO) {
        this.servicioDAO = Objects.requireNonNull(
                servicioDAO,
                "El DAO de servicios no puede ser nulo."
        );
    }

    public List<Servicio> listarServicios() {
        return servicioDAO.obtenerTodos();
    }

    public List<Servicio> listarServiciosActivos() {
        return servicioDAO.obtenerServiciosActivos();
    }

    public Optional<Servicio> buscarServicioPorId(Integer id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un ID de servicio válido."
            );
        }

        return servicioDAO.obtenerPorId(id);
    }

    public void registrarServicio(Servicio servicio) {

        validarServicio(servicio);

        if (servicio.getIdServicio() != null) {
            throw new IllegalArgumentException(
                    "Un servicio nuevo no debe tener un ID asignado."
            );
        }

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

        if (servicio == null
                || servicio.getIdServicio() == null
                || servicio.getIdServicio() <= 0) {
            throw new IllegalArgumentException(
                    "Se requiere un ID válido para actualizar el servicio."
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

        if (servicio == null
                || servicio.getIdServicio() == null
                || servicio.getIdServicio() <= 0) {
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

        if (servicio.getIdCliente() == null
                || servicio.getIdCliente() <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un cliente válido."
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
