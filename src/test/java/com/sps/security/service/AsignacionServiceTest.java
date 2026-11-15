package com.sps.security.service;

import com.sps.security.dao.AsignacionDAO;
import com.sps.security.model.operative.Asignacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AsignacionServiceTest {

    private AsignacionService service;
    private AsignacionDAOFalso dao;
    private ValidadorFalso validador;

    private final LocalDate fecha =
            LocalDate.of(2026, 11, 14);

    @BeforeEach
    void preparar() {
        dao = new AsignacionDAOFalso();
        validador = new ValidadorFalso();
        service = new AsignacionService(dao, validador);
    }

    @Test
    void registrarAsignacionCorrectamente() {
        service.registrarAsignacion(1, 1, 1, fecha);

        List<Asignacion> asignaciones =
                service.listarAsignaciones();

        assertEquals(1, asignaciones.size());
        assertEquals(1, asignaciones.get(0).getIdGuardia());
        assertEquals(1, asignaciones.get(0).getIdServicio());
        assertEquals(1, asignaciones.get(0).getIdJornada());
        assertEquals(fecha,
                asignaciones.get(0).getFechaAsignacion());
    }

    @Test
    void rechazarGuardiaInactivo() {
        validador.guardiaActivo = false;

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarAsignacion(
                        1, 1, 1, fecha
                )
        );

        assertTrue(service.listarAsignaciones().isEmpty());
    }

    @Test
    void rechazarServicioInactivo() {
        validador.servicioActivo = false;

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarAsignacion(
                        1, 1, 1, fecha
                )
        );
    }

    @Test
    void rechazarJornadaInactiva() {
        validador.jornadaActiva = false;

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarAsignacion(
                        1, 1, 1, fecha
                )
        );
    }

    @Test
    void rechazarAsignacionDuplicada() {
        service.registrarAsignacion(1, 1, 1, fecha);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarAsignacion(
                        1, 2, 1, fecha
                )
        );

        assertEquals(1, service.listarAsignaciones().size());
    }

    @Test
    void permitirDistintaJornada() {
        service.registrarAsignacion(1, 1, 1, fecha);
        service.registrarAsignacion(1, 2, 2, fecha);

        assertEquals(2, service.listarAsignaciones().size());
    }

    @Test
    void rechazarFechaNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarAsignacion(
                        1, 1, 1, null
                )
        );
    }

    @Test
    void actualizarAsignacionCorrectamente() {
        service.registrarAsignacion(1, 1, 1, fecha);

        Integer id = service.listarAsignaciones()
                .get(0).getIdAsignacion();

        service.actualizarAsignacion(
                id,
                1,
                2,
                2,
                fecha.plusDays(1)
        );

        Asignacion actualizada =
                service.obtenerAsignacionPorId(id).orElseThrow();

        assertEquals(2, actualizada.getIdServicio());
        assertEquals(2, actualizada.getIdJornada());
        assertEquals(
                fecha.plusDays(1),
                actualizada.getFechaAsignacion()
        );
    }

    @Test
    void eliminarAsignacionCorrectamente() {
        service.registrarAsignacion(1, 1, 1, fecha);

        Integer id = service.listarAsignaciones()
                .get(0).getIdAsignacion();

        service.eliminarAsignacion(id);

        assertTrue(service.listarAsignaciones().isEmpty());
        assertTrue(service.obtenerAsignacionPorId(id).isEmpty());
    }

    @Test
    void consultarAsignacionesPorGuardia() {
        service.registrarAsignacion(1, 1, 1, fecha);
        service.registrarAsignacion(2, 1, 1, fecha);

        List<Asignacion> resultado =
                service.listarAsignacionesPorGuardia(1);

        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getIdGuardia());
    }

    private static class ValidadorFalso
            implements AsignacionService.ValidadorReferencias {

        boolean guardiaActivo = true;
        boolean servicioActivo = true;
        boolean jornadaActiva = true;

        @Override
        public boolean guardiaActivo(Integer idGuardia) {
            return guardiaActivo;
        }

        @Override
        public boolean servicioActivo(Integer idServicio) {
            return servicioActivo;
        }

        @Override
        public boolean jornadaActiva(Integer idJornada) {
            return jornadaActiva;
        }
    }

    private static class AsignacionDAOFalso
            implements AsignacionDAO {

        private final List<Asignacion> asignaciones =
                new ArrayList<>();

        private int siguienteId = 1;

        @Override
        public boolean guardar(Asignacion asignacion) {
            asignacion.setIdAsignacion(siguienteId++);
            asignaciones.add(asignacion);
            return true;
        }

        @Override
        public boolean actualizar(Asignacion asignacion) {
            for (int i = 0; i < asignaciones.size(); i++) {
                if (Objects.equals(
                        asignaciones.get(i).getIdAsignacion(),
                        asignacion.getIdAsignacion()
                )) {
                    asignaciones.set(i, asignacion);
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean eliminar(Asignacion asignacion) {
            return asignaciones.removeIf(
                    a -> Objects.equals(
                            a.getIdAsignacion(),
                            asignacion.getIdAsignacion()
                    )
            );
        }

        @Override
        public Optional<Asignacion> obtenerPorId(Integer id) {
            return asignaciones.stream()
                    .filter(a -> Objects.equals(
                            a.getIdAsignacion(), id
                    ))
                    .findFirst();
        }

        @Override
        public List<Asignacion> obtenerTodos() {
            return new ArrayList<>(asignaciones);
        }

        @Override
        public List<Asignacion> obtenerAsignacionesPorGuardia(
                Integer idGuardia) {

            return asignaciones.stream()
                    .filter(a -> Objects.equals(
                            a.getIdGuardia(), idGuardia
                    ))
                    .toList();
        }
    }
}
