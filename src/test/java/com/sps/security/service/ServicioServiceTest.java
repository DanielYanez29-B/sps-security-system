
package com.sps.security.service;

import com.sps.security.dao.ServicioDAO;
import com.sps.security.model.operative.Servicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioServiceTest {

    private ServicioService service;
    private ServicioDAOFalso dao;

    @BeforeEach
    void preparar() {
        dao = new ServicioDAOFalso();
        service = new ServicioService(dao);
    }

    @Test
    void registrarServicioCorrectamente() {
        Servicio servicio = crearServicio(1, "Monitoreo de seguridad");

        service.registrarServicio(servicio);

        assertEquals(1, service.listarServicios().size());
        assertEquals("ACTIVO", servicio.getEstado());
    }

    @Test
    void rechazarServicioNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarServicio(null)
        );
    }

    @Test
    void rechazarNombreVacio() {
        Servicio servicio = crearServicio(1, "");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarServicio(servicio)
        );

        assertTrue(service.listarServicios().isEmpty());
    }

    @Test
    void rechazarClienteNulo() {
        Servicio servicio = crearServicio(null, "Monitoreo");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarServicio(servicio)
        );
    }

    @Test
    void listarServiciosCorrectamente() {
        dao.guardar(crearServicio(1, "Monitoreo"));
        dao.guardar(crearServicio(2, "Vigilancia"));

        assertEquals(2, service.listarServicios().size());
    }

    @Test
    void listarServiciosActivosCorrectamente() {
        Servicio activo = crearServicio(1, "Monitoreo");
        Servicio inactivo = crearServicio(2, "Vigilancia");
        inactivo.setEstado("INACTIVO");

        dao.guardar(activo);
        dao.guardar(inactivo);

        assertEquals(1, service.listarServiciosActivos().size());
    }

    @Test
    void buscarServicioPorId() {
        dao.guardar(crearServicio(1, "Monitoreo"));

        Optional<Servicio> resultado = service.buscarServicioPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Monitoreo", resultado.get().getNombreServicio());
    }

    @Test
    void rechazarBusquedaConIdNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.buscarServicioPorId(null)
        );
    }

    @Test
    void actualizarServicioCorrectamente() {
        dao.guardar(crearServicio(1, "Monitoreo"));

        Servicio actualizado = crearServicio(1, "Monitoreo actualizado");

        service.actualizarServicio(actualizado);

        assertEquals(
                "Monitoreo actualizado",
                service.buscarServicioPorId(1).get().getNombreServicio()
        );
    }

    @Test
    void eliminarServicioCorrectamente() {
        Servicio servicio = crearServicio(1, "Monitoreo");
        dao.guardar(servicio);

        service.eliminarServicio(servicio);

        assertTrue(service.listarServicios().isEmpty());
    }

    private Servicio crearServicio(Integer idCliente, String nombre) {
        return new Servicio(
                1,
                idCliente,
                nombre,
                "Av. Principal 100",
                LocalDate.of(2026, 11, 16),
                "ACTIVO"
        );
    }

    private static class ServicioDAOFalso implements ServicioDAO {

        private final List<Servicio> servicios = new ArrayList<>();

        @Override
        public boolean guardar(Servicio servicio) {
            servicios.add(servicio);
            return true;
        }

        @Override
        public Optional<Servicio> obtenerPorId(Integer id) {
            return servicios.stream()
                    .filter(s -> Objects.equals(s.getIdServicio(), id))
                    .findFirst();
        }

        @Override
        public List<Servicio> obtenerTodos() {
            return new ArrayList<>(servicios);
        }

        @Override
        public boolean actualizar(Servicio servicio) {
            for (int i = 0; i < servicios.size(); i++) {
                if (Objects.equals(
                        servicios.get(i).getIdServicio(),
                        servicio.getIdServicio()
                )) {
                    servicios.set(i, servicio);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean eliminar(Servicio servicio) {
            return servicios.removeIf(
                    s -> Objects.equals(
                            s.getIdServicio(),
                            servicio.getIdServicio()
                    )
            );
        }

        @Override
        public List<Servicio> obtenerServiciosActivos() {
            return servicios.stream()
                    .filter(s -> "ACTIVO".equals(s.getEstado()))
                    .toList();
        }
    }
}
