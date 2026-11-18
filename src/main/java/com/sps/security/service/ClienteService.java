package com.sps.security.service;

import com.sps.security.dao.ClienteDAO;
import com.sps.security.dao.impl.ClienteDAOImpl;
import com.sps.security.model.users.Cliente;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAOImpl();
    }

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = Objects.requireNonNull(
                clienteDAO, "El DAO de clientes no puede ser nulo."
        );
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.obtenerTodos();
    }

    public void registrarCliente(Cliente cliente) {
        validarCliente(cliente);

        if (cliente.getIdcliente() != null) {
            throw new IllegalArgumentException(
                    "El ID de un cliente nuevo debe ser generado por MySQL."
            );
        }

        validarEmail(cliente.getEmail());
        validarCorreoDuplicado(cliente.getEmail(), null);

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

        validarCorreoDuplicado(
                cliente.getEmail(),
                cliente.getIdcliente()
        );

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

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un correo electrónico."
            );
        }

        String correo = email.trim();
        validarEmail(correo);

        return clienteDAO.buscarPorEmail(correo);
    }

    private void validarCliente(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no puede ser nulo."
            );
        }

        if (cliente.getNombre() == null ||
                cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre o razón social es obligatorio."
            );
        }
    }

    private void validarEmail(String email) {

        if (email == null || email.isBlank()) {
            return;
        }

        String regex =
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (!email.trim().matches(regex)) {
            throw new IllegalArgumentException(
                    "El formato del correo electrónico es inválido."
            );
        }
    }

    private void validarCorreoDuplicado(
            String email,
            Integer idClienteActual) {

        if (email == null || email.isBlank()) {
            return;
        }

        Optional<Cliente> existente =
                clienteDAO.buscarPorEmail(email.trim());

        if (existente.isPresent() &&
                !Objects.equals(
                        existente.get().getIdcliente(),
                        idClienteActual)) {

            throw new IllegalArgumentException(
                    "Ya existe otro cliente registrado con ese correo electrónico."
            );
        }
    }
}
