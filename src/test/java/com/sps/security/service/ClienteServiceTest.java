package com.sps.security.service;

import com.sps.security.dao.ClienteDAO;
import com.sps.security.model.users.Cliente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteServiceTest {

    private ClienteService service;
    private ClienteDAOFalso dao;

    @BeforeEach
    void preparar() {
        dao = new ClienteDAOFalso();
        service = new ClienteService(dao);
    }

    @Test
    void registrarClienteCorrectamente() {
        Cliente cliente = crearCliente(null, "Empresa ABC",
                "ABC010101ABC", "empresa@gmail.com");

        service.registrarCliente(cliente);

        assertEquals(1, service.listarClientes().size());
        assertEquals("Empresa ABC",
                service.listarClientes().get(0).getNombre());
    }

    @Test
    void rechazarNombreVacio() {
        Cliente cliente = crearCliente(null, "",
                "ABC010101ABC", "empresa@gmail.com");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarCliente(cliente)
        );

        assertTrue(service.listarClientes().isEmpty());
    }

    @Test
    void rechazarCorreoInvalido() {
        Cliente cliente = crearCliente(null, "Empresa ABC",
                "ABC010101ABC", "correo-invalido");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarCliente(cliente)
        );

        assertTrue(service.listarClientes().isEmpty());
    }

    @Test
    void rechazarCorreoDuplicado() {
        service.registrarCliente(
                crearCliente(null, "Empresa ABC",
                        "ABC010101ABC", "empresa@gmail.com")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarCliente(
                        crearCliente(null, "Empresa XYZ",
                                "XYZ010101XYZ", "empresa@gmail.com")
                )
        );

        assertEquals(1, service.listarClientes().size());
    }

    @Test
    void listarClientesCorrectamente() {
        service.registrarCliente(
                crearCliente(null, "Empresa ABC",
                        "ABC010101ABC", "abc@gmail.com")
        );

        service.registrarCliente(
                crearCliente(null, "Empresa XYZ",
                        "XYZ010101XYZ", "xyz@gmail.com")
        );

        assertEquals(2, service.listarClientes().size());
    }

    @Test
    void buscarClientePorEmail() {
        service.registrarCliente(
                crearCliente(null, "Empresa ABC",
                        "ABC010101ABC", "empresa@gmail.com")
        );

        Optional<Cliente> resultado =
                service.buscarClientePorEmail("empresa@gmail.com");

        assertTrue(resultado.isPresent());
        assertEquals("Empresa ABC", resultado.get().getNombre());
    }

    @Test
    void actualizarClienteCorrectamente() {
        Cliente cliente = crearCliente(1, "Empresa ABC",
                "ABC010101ABC", "abc@gmail.com");

        dao.guardar(cliente);

        cliente.setNombre("Empresa Actualizada");

        service.actualizarCliente(cliente);

        assertEquals("Empresa Actualizada",
                service.listarClientes().get(0).getNombre());
    }

    @Test
    void eliminarClienteCorrectamente() {
        Cliente cliente = crearCliente(1, "Empresa ABC",
                "ABC010101ABC", "abc@gmail.com");

        dao.guardar(cliente);

        service.eliminarCliente(cliente);

        assertTrue(service.listarClientes().isEmpty());
    }

    @Test
    void rechazarClienteNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarCliente(null)
        );
    }

    private Cliente crearCliente(Integer id, String nombre,
                                 String rfc, String email) {
        return new Cliente(
                id,
                nombre,
                rfc,
                "3312345678",
                email,
                "Av. Principal",
                "100",
                null,
                "Centro",
                "Guadalajara",
                "Jalisco",
                "44100"
        );
    }

    private static class ClienteDAOFalso implements ClienteDAO {

        private final List<Cliente> clientes = new ArrayList<>();

        @Override
        public boolean guardar(Cliente cliente) {
            clientes.add(cliente);
            return true;
        }

        @Override
        public Optional<Cliente> obtenerPorId(Integer id) {
            return clientes.stream()
                    .filter(c -> Objects.equals(c.getIdcliente(), id))
                    .findFirst();
        }

        @Override
        public List<Cliente> obtenerTodos() {
            return new ArrayList<>(clientes);
        }

        @Override
        public boolean actualizar(Cliente cliente) {
            for (int i = 0; i < clientes.size(); i++) {
                if (Objects.equals(
                        clientes.get(i).getIdcliente(),
                        cliente.getIdcliente())) {
                    clientes.set(i, cliente);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean eliminar(Cliente cliente) {
            return clientes.removeIf(
                    c -> Objects.equals(
                            c.getIdcliente(),
                            cliente.getIdcliente())
            );
        }

        @Override
        public Optional<Cliente> buscarPorEmail(String email) {
            return clientes.stream()
                    .filter(c -> c.getEmail() != null)
                    .filter(c -> c.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        }
    }
}
