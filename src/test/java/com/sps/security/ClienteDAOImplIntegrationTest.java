package com.sps.security;

import com.sps.security.dao.impl.ClienteDAOImpl;
import com.sps.security.model.users.Cliente;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClienteDAOImplIntegrationTest {

    @Test
    void probarCrudRealDeClientes() {

        ClienteDAOImpl dao = new ClienteDAOImpl();

        String identificador = UUID.randomUUID().toString()
                .replace("-", "").substring(0, 12);

        String email = "prueba-" + identificador + "@example.com";

        Cliente cliente = new Cliente(
                null,
                "Cliente de prueba SPS",
                null,
                "3312345678",
                email,
                "Calle de prueba",
                "123",
                "Centro",
                "Guadalajara",
                "Jalisco",
                "44100"
        );

        try {
            // 1. Guardar
            assertTrue(dao.guardar(cliente),
                    "No se pudo guardar el cliente");

            assertNotNull(cliente.getIdcliente(),
                    "No se generó el ID");

            Integer id = cliente.getIdcliente();

            // 2. Buscar por ID
            Cliente encontrado = dao.obtenerPorId(id).orElseThrow();

            assertEquals("Cliente de prueba SPS",
                    encontrado.getNombre());

            // 3. Buscar por correo
            assertTrue(dao.buscarPorEmail(email).isPresent());

            // 4. Consultar todos
            assertTrue(dao.obtenerTodos().stream()
                    .anyMatch(c -> id.equals(c.getIdcliente())));

            // 5. Actualizar
            cliente.setNombre("Cliente SPS actualizado");

            assertTrue(dao.actualizar(cliente));

            Cliente actualizado = dao.obtenerPorId(id).orElseThrow();

            assertEquals("Cliente SPS actualizado",
                    actualizado.getNombre());

            // 6. Eliminar
            assertTrue(dao.eliminar(cliente));

            assertTrue(dao.obtenerPorId(id).isEmpty());

        } finally {
            // Limpieza si alguna comprobación falla.
            // Solo elimina el cliente temporal identificado
            // por el correo único de esta ejecución.
            dao.buscarPorEmail(email).ifPresent(dao::eliminar);
        }
    }
}
