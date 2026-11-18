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
        assertNotNull(servicio.getIdServicio());
    }

    @Test
    void registrarServicioConEstadoPorDefecto() {
        Servicio servicio = crearServicio(1, "Monitoreo");
        servicio.setEstado(null);

        service.registrarServicio(servicio);

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
    void rechazarClienteConIdInvalido() {
        Servicio servicio = crearServicio(0, "Monitoreo");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarServicio(servicio)
        );
    }

    @Test
    void rechazarDireccionVacia() {
        Servicio servicio = crearServicio(1, "Monitoreo");
        servicio.setDireccion("");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarServicio(servicio)
        );
    }

    @Test
    void rechazarFechaInicioNula() {
        Servicio servicio = crearServicio(1, "Monitoreo");
        servicio.setFechaInicio(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarServicio(servicio)
        );
    }

    @Test
    void rechazarServicioNuevoConId() {
        Servicio servicio = crearServicio(1, "Monitoreo");
        servicio.setIdServicio(10);

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
        assertEquals(
                "Monitoreo",
                service.listarServiciosActivos().get(0).getNombreServicio()
        );
    }

    @Test
    void buscarServicioPorId() {
        Servicio servicio = crearServicio(1, "Monitoreo");
        dao.guardar(servicio);

        Optional<Servicio> resultado =
                service.buscarServicioPorId(servicio.getIdServicio());

        assertTrue(resultado.isPresent());
        assertEquals("Monitoreo", resultado.get().getNombreServicio());
    }

    @Test
    void buscarServicioInexistente() {
        Optional<Servicio> resultado = service.buscarServicioPorId(999);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void rechazarBusquedaConIdNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.buscarServicioPorId(null)
        );
    }

    @Test
    void rechazarBusquedaConIdInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.buscarServicioPorId(0)
        );
    }

    @Test
    void actualizarServicioCorrectamente() {
        Servicio original = crearServicio(1, "Monitoreo");
        dao.guardar(original);

        Servicio actualizado = crearServicio(
                1, "Monitoreo actualizado"
        );

        actualizado.setIdServicio(original.getIdServicio());

        service.actualizarServicio(actualizado);

        assertEquals(
                "Monitoreo actualizado",
                service.buscarServicioPorId(
                        original.getIdServicio()
                ).orElseThrow().getNombreServicio()
        );
    }

    @Test
    void rechazarActualizacionSinId() {
        Servicio servicio = crearServicio(1, "Monitoreo");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.actualizarServicio(servicio)
        );
    }

    @Test
    void eliminarServicioCorrectamente() {
        Servicio servicio = crearServicio(1, "Monitoreo");
        dao.guardar(servicio);

        service.eliminarServicio(servicio);

        assertTrue(service.listarServicios().isEmpty());
    }

    @Test
    void rechazarEliminacionSinId() {
        Servicio servicio = crearServicio(1, "Monitoreo");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.eliminarServicio(servicio)
        );
    }

    private Servicio crearServicio(Integer idCliente, String nombre) {
        return new Servicio(
                null,
                idCliente,
                nombre,
                "Av. Principal 100",
                LocalDate.of(2026, 11, 16),
                "ACTIVO"
        );
    }

    private static class ServicioDAOFalso implements ServicioDAO {

        private final List<Servicio> servicios = new ArrayList<>();
        private int siguienteId = 1;

        @Override
        public boolean guardar(Servicio servicio) {
            if (servicio == null || servicio.getIdServicio() != null) {
                return false;
            }

            servicio.setIdServicio(siguienteId++);
            servicios.add(servicio);
            return true;
        }

        @Override
        public Optional<Servicio> obtenerPorId(Integer id) {
            return servicios.stream()
                    .filter(s -> Objects.equals(
                            s.getIdServicio(), id
                    ))
                    .findFirst();
        }

        @Override
        public List<Servicio> obtenerTodos() {
            return new ArrayList<>(servicios);
        }

        @Override
        public boolean actualizar(Servicio servicio) {
            if (servicio == null || servicio.getIdServicio() == null) {
                return false;
            }

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
            if (servicio == null || servicio.getIdServicio() == null) {
                return false;
            }

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
