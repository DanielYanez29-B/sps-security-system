package com.sps.security.service;

import com.sps.security.dao.ClienteDAO;
import com.sps.security.model.users.Cliente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        Cliente cliente = crearCliente(
                null, "Empresa ABC",
                "ABC010101ABC", "empresa@gmail.com"
        );

        service.registrarCliente(cliente);

        assertEquals(1, service.listarClientes().size());
        assertEquals("Empresa ABC",
                service.listarClientes().get(0).getNombre());
        assertNotNull(cliente.getIdcliente());
    }

    @Test
    void rechazarNombreVacio() {
        Cliente cliente = crearCliente(
                null, "", "ABC010101ABC", "empresa@gmail.com"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarCliente(cliente)
        );

        assertTrue(service.listarClientes().isEmpty());
    }

    @Test
    void rechazarCorreoInvalido() {
        Cliente cliente = crearCliente(
                null, "Empresa ABC",
                "ABC010101ABC", "correo-invalido"
        );

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
    void rechazarClienteNuevoConId() {
        Cliente cliente = crearCliente(
                10, "Empresa ABC",
                "ABC010101ABC", "empresa@gmail.com"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarCliente(cliente)
        );
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
    void rechazarBusquedaConCorreoVacio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.buscarClientePorEmail("")
        );
    }

    @Test
    void actualizarClienteCorrectamente() {
        Cliente cliente = crearCliente(
                null, "Empresa ABC",
                "ABC010101ABC", "abc@gmail.com"
        );

        dao.guardar(cliente);

        cliente.setNombre("Empresa Actualizada");

        service.actualizarCliente(cliente);

        assertEquals(
                "Empresa Actualizada",
                service.listarClientes().get(0).getNombre()
        );
    }

    @Test
    void rechazarActualizacionSinId() {
        Cliente cliente = crearCliente(
                null, "Empresa ABC",
                "ABC010101ABC", "abc@gmail.com"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.actualizarCliente(cliente)
        );
    }

    @Test
    void rechazarActualizacionConCorreoDuplicado() {
        Cliente primero = crearCliente(
                null, "Empresa ABC",
                "ABC010101ABC", "abc@gmail.com"
        );

        Cliente segundo = crearCliente(
                null, "Empresa XYZ",
                "XYZ010101XYZ", "xyz@gmail.com"
        );

        dao.guardar(primero);
        dao.guardar(segundo);

        segundo.setEmail("abc@gmail.com");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.actualizarCliente(segundo)
        );
    }

    @Test
    void eliminarClienteCorrectamente() {
        Cliente cliente = crearCliente(
                null, "Empresa ABC",
                "ABC010101ABC", "abc@gmail.com"
        );

        dao.guardar(cliente);

        service.eliminarCliente(cliente);

        assertTrue(service.listarClientes().isEmpty());
    }

    @Test
    void rechazarEliminacionSinId() {
        Cliente cliente = crearCliente(
                null, "Empresa ABC",
                "ABC010101ABC", "abc@gmail.com"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.eliminarCliente(cliente)
        );
    }

    @Test
    void rechazarClienteNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarCliente(null)
        );
    }

    private Cliente crearCliente(
            Integer id, String nombre,
            String rfc, String email) {

        return new Cliente(
                id,
                nombre,
                rfc,
                "3312345678",
                email,
                "Av. Principal",
                "100",
                "Centro",
                "Guadalajara",
                "Jalisco",
                "44100"
        );
    }

    private static class ClienteDAOFalso implements ClienteDAO {

        private final List<Cliente> clientes = new ArrayList<>();
        private int siguienteId = 1;

        @Override
        public boolean guardar(Cliente cliente) {
            if (cliente == null) {
                return false;
            }

            if (cliente.getIdcliente() == null) {
                cliente.setIdcliente(siguienteId++);
            }

            clientes.add(cliente);
            return true;
        }

        @Override
        public Optional<Cliente> obtenerPorId(Integer id) {
            return clientes.stream()
                    .filter(c -> Objects.equals(
                            c.getIdcliente(), id))
                    .findFirst();
        }

        @Override
        public List<Cliente> obtenerTodos() {
            return new ArrayList<>(clientes);
        }

        @Override
        public boolean actualizar(Cliente cliente) {
            if (cliente == null || cliente.getIdcliente() == null) {
                return false;
            }

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
            if (cliente == null || cliente.getIdcliente() == null) {
                return false;
            }

            return clientes.removeIf(
                    c -> Objects.equals(
                            c.getIdcliente(),
                            cliente.getIdcliente())
            );
        }

        @Override
        public Optional<Cliente> buscarPorEmail(String email) {
            if (email == null) {
                return Optional.empty();
            }

            return clientes.stream()
                    .filter(c -> c.getEmail() != null)
                    .filter(c -> c.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        }
    }
}
