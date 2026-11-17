package com.sps.security.service;

import com.sps.security.dao.ClienteDAO;
import com.sps.security.dao.impl.ClienteDAOImpl;
import com.sps.security.model.users.Cliente;

import java.util.List;
import java.util.Optional;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.obtenerTodos();
    }

    public void registrarCliente(Cliente cliente) {
        validarCliente(cliente);
        validarEmail(cliente.getEmail());

        if (cliente.getEmail() != null && !cliente.getEmail().isBlank()
                && clienteDAO.buscarPorEmail(cliente.getEmail()).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente registrado con ese correo electrónico."
            );
        }

        boolean guardado = clienteDAO.guardar(cliente);

        if (!guardado) {
            throw new RuntimeException(
                    "Error en base de datos: No se pudo registrar el cliente."
            );
        }
    }

    public void actualizarCliente(Cliente cliente) {
        if (cliente == null || cliente.getIdcliente() == null) {
            throw new IllegalArgumentException(
                    "Se requiere el ID del cliente para actualizar."
            );
        }

        validarCliente(cliente);
        validarEmail(cliente.getEmail());

        if (cliente.getEmail() != null && !cliente.getEmail().isBlank()) {
            Optional<Cliente> existente =
                    clienteDAO.buscarPorEmail(cliente.getEmail());

            if (existente.isPresent()
                    && !existente.get().getIdcliente().equals(cliente.getIdcliente())) {
                throw new IllegalArgumentException(
                        "Ese correo electrónico ya pertenece a otro cliente."
                );
            }
        }

        boolean actualizado = clienteDAO.actualizar(cliente);

        if (!actualizado) {
            throw new RuntimeException(
                    "Error en base de datos: No se pudo actualizar el cliente."
            );
        }
    }

    public void eliminarCliente(Cliente cliente) {
        if (cliente == null || cliente.getIdcliente() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un cliente válido para eliminar."
            );
        }

        boolean eliminado = clienteDAO.eliminar(cliente);

        if (!eliminado) {
            throw new RuntimeException(
                    "Error en base de datos: No se pudo eliminar el cliente."
            );
        }
    }

    public Optional<Cliente> buscarClientePorEmail(String email) {
        validarEmail(email);

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un correo electrónico."
            );
        }

        return clienteDAO.buscarPorEmail(email);
    }

    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no puede ser nulo."
            );
        }

        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre o razón social es obligatorio."
            );
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

        if (!email.matches(regex)) {
            throw new IllegalArgumentException(
                    "El formato del correo electrónico es inválido."
            );
        }
    }
}
