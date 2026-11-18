package com.sps.security;

import com.sps.security.dao.impl.ClienteDAOImpl;
import com.sps.security.dao.impl.ServicioDAOImpl;
import com.sps.security.dao.impl.ElementoSupervisadoDAOImpl;
import com.sps.security.model.users.Cliente;
import com.sps.security.model.operative.Servicio;
import com.sps.security.model.supervised.ElementoSupervisado;
import com.sps.security.model.enums.TipoElemento;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ElementoSupervisadoDAOImplIntegrationTest {

    @Test
    void probarCrudRealDeElementosSupervisados() {

        ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
        ServicioDAOImpl servicioDAO = new ServicioDAOImpl();
        ElementoSupervisadoDAOImpl elementoDAO =
                new ElementoSupervisadoDAOImpl();

        String codigo = UUID.randomUUID().toString()
                .replace("-", "").substring(0, 12);

        Cliente cliente = new Cliente(
                null,
                "Cliente temporal Elementos SPS",
                null,
                "3312345678",
                "elemento-prueba-" + codigo + "@example.com",
                "Calle Prueba",
                "123",
                "Centro",
                "Guadalajara",
                "Jalisco",
                "44100"
        );

        Servicio servicio = null;
        ElementoSupervisado elemento = null;

        try {
            // 1. Crear cliente temporal
            assertTrue(clienteDAO.guardar(cliente));
            assertNotNull(cliente.getIdcliente());

            // 2. Crear servicio temporal
            servicio = new Servicio(
                    null,
                    cliente.getIdcliente(),
                    "Servicio prueba elementos " + codigo,
                    "Dirección temporal",
                    LocalDate.of(2026, 11, 18),
                    "ACTIVO"
            );

            assertTrue(servicioDAO.guardar(servicio));
            assertNotNull(servicio.getIdServicio());

            // 3. Crear elemento supervisado
            elemento = new ElementoSupervisado(
                    null,
                    servicio.getIdServicio(),
                    TipoElemento.VEHICULO,
                    "VEH-PRUEBA-" + codigo,
                    "Vehículo temporal de prueba",
                    true
            );

            assertTrue(elementoDAO.guardar(elemento));
            assertNotNull(elemento.getIdElemento());

            Integer idElemento = elemento.getIdElemento();

            // 4. Buscar elemento por ID
            ElementoSupervisado encontrado = elementoDAO
                    .obtenerPorId(idElemento)
                    .orElseThrow();

            assertEquals(TipoElemento.VEHICULO,
                    encontrado.getTipoElemento());

            assertEquals(elemento.getIdentificador(),
                    encontrado.getIdentificador());

            // 5. Consultar todos los elementos
            assertTrue(elementoDAO.obtenerTodos().stream()
                    .anyMatch(e ->
                            idElemento.equals(e.getIdElemento())));

            // 6. Consultar elementos por servicio
            assertTrue(elementoDAO
                    .obtenerPorServicio(servicio.getIdServicio())
                    .stream()
                    .anyMatch(e ->
                            idElemento.equals(e.getIdElemento())));

            // 7. Actualizar elemento
            elemento.setDescripcion("Vehículo actualizado");
            elemento.setEstadoActivo(false);

            assertTrue(elementoDAO.actualizar(elemento));

            ElementoSupervisado actualizado = elementoDAO
                    .obtenerPorId(idElemento)
                    .orElseThrow();

            assertEquals("Vehículo actualizado",
                    actualizado.getDescripcion());

            assertEquals(Boolean.FALSE,
                    actualizado.getEstadoActivo());

            // 8. Eliminar elemento
            assertTrue(elementoDAO.eliminar(elemento));

            assertTrue(elementoDAO.obtenerPorId(idElemento).isEmpty());

        } finally {
            // Limpiar únicamente los registros temporales
            if (elemento != null
                    && elemento.getIdElemento() != null) {
                elementoDAO.eliminar(elemento);
            }

            if (servicio != null
                    && servicio.getIdServicio() != null) {
                servicioDAO.eliminar(servicio);
            }

            if (cliente.getIdcliente() != null) {
                clienteDAO.eliminar(cliente);
            }
        }
    }
}
