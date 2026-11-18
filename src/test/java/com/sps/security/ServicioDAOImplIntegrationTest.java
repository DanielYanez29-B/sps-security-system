package com.sps.security;

import com.sps.security.dao.impl.ClienteDAOImpl;
import com.sps.security.dao.impl.ServicioDAOImpl;
import com.sps.security.model.users.Cliente;
import com.sps.security.model.operative.Servicio;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServicioDAOImplIntegrationTest {

    @Test
    void probarCrudRealDeServicios() {

        ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
        ServicioDAOImpl servicioDAO = new ServicioDAOImpl();

        String codigo = UUID.randomUUID().toString()
                .replace("-", "").substring(0, 12);

        Cliente cliente = new Cliente(
                null,
                "Cliente temporal SPS",
                null,
                "3312345678",
                "servicio-prueba-" + codigo + "@example.com",
                "Calle Prueba",
                "123",
                "Centro",
                "Guadalajara",
                "Jalisco",
                "44100"
        );

        Servicio servicio = null;

        try {
            // Crear un cliente exclusivo para esta prueba
            assertTrue(clienteDAO.guardar(cliente));
            assertNotNull(cliente.getIdcliente());

            servicio = new Servicio(
                    null,
                    cliente.getIdcliente(),
                    "Vigilancia de prueba " + codigo,
                    "Dirección temporal de prueba",
                    LocalDate.of(2026, 11, 18),
                    "ACTIVO"
            );

            // 1. Guardar servicio
            assertTrue(servicioDAO.guardar(servicio));
            assertNotNull(servicio.getIdServicio());

            Integer idServicio = servicio.getIdServicio();

            // 2. Buscar por ID
            Servicio encontrado = servicioDAO
                    .obtenerPorId(idServicio)
                    .orElseThrow();

            assertEquals(servicio.getNombreServicio(),
                    encontrado.getNombreServicio());

            assertEquals(cliente.getIdcliente(),
                    encontrado.getIdCliente());

            // 3. Consultar todos
            assertTrue(servicioDAO.obtenerTodos().stream()
                    .anyMatch(s ->
                            idServicio.equals(s.getIdServicio())));

            // 4. Consultar servicios activos
            assertTrue(servicioDAO.obtenerServiciosActivos().stream()
                    .anyMatch(s ->
                            idServicio.equals(s.getIdServicio())));

            // 5. Actualizar
            servicio.setNombreServicio("Servicio actualizado " + codigo);
            servicio.setEstado("INACTIVO");

            assertTrue(servicioDAO.actualizar(servicio));

            Servicio actualizado = servicioDAO
                    .obtenerPorId(idServicio)
                    .orElseThrow();

            assertEquals(servicio.getNombreServicio(),
                    actualizado.getNombreServicio());

            assertEquals("INACTIVO", actualizado.getEstado());

            // Ya no debe aparecer entre los activos
            assertFalse(servicioDAO.obtenerServiciosActivos().stream()
                    .anyMatch(s ->
                            idServicio.equals(s.getIdServicio())));

            // 6. Eliminar servicio
            assertTrue(servicioDAO.eliminar(servicio));

            assertTrue(servicioDAO.obtenerPorId(idServicio).isEmpty());

        } finally {
            // Limpiar el servicio creado por la prueba
            if (servicio != null
                    && servicio.getIdServicio() != null) {
                servicioDAO.eliminar(servicio);
            }

            // Limpiar el cliente temporal
            if (cliente.getIdcliente() != null) {
                clienteDAO.eliminar(cliente);
            }
        }
    }
}
