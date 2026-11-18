package com.sps.security.service;

import com.sps.security.dao.ElementoSupervisadoDAO;
import com.sps.security.model.enums.TipoElemento;
import com.sps.security.model.supervised.ElementoSupervisado;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ElementoSupervisadoServiceTest {

    private ElementoSupervisadoService service;
    private ElementoDAOFalso dao;

    @BeforeEach
    void preparar() {
        dao = new ElementoDAOFalso();
        service = new ElementoSupervisadoService(dao);
    }

    @Test
    void registrarElementoCorrectamente() {
        ElementoSupervisado elemento = crearElemento(
                10, "ABC-123"
        );

        service.registrarElemento(elemento);

        assertEquals(1, service.listarElementos().size());
        assertTrue(elemento.getEstadoActivo());
        assertNotNull(elemento.getIdElemento());
    }

    @Test
    void registrarElementoActivoPorDefecto() {
        ElementoSupervisado elemento = crearElemento(
                10, "ABC-123"
        );
        elemento.setEstadoActivo(null);

        service.registrarElemento(elemento);

        assertTrue(elemento.getEstadoActivo());
    }

    @Test
    void rechazarElementoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarElemento(null)
        );
    }

    @Test
    void rechazarServicioNulo() {
        ElementoSupervisado elemento = crearElemento(
                null, "ABC-123"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarElemento(elemento)
        );
    }

    @Test
    void rechazarServicioConIdInvalido() {
        ElementoSupervisado elemento = crearElemento(
                0, "ABC-123"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarElemento(elemento)
        );
    }

    @Test
    void rechazarTipoElementoNulo() {
        ElementoSupervisado elemento = crearElemento(
                10, "ABC-123"
        );
        elemento.setTipoElemento(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarElemento(elemento)
        );
    }

    @Test
    void rechazarIdentificadorVacio() {
        ElementoSupervisado elemento = crearElemento(
                10, ""
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarElemento(elemento)
        );
    }

    @Test
    void rechazarElementoNuevoConId() {
        ElementoSupervisado elemento = crearElemento(
                10, "ABC-123"
        );
        elemento.setIdElemento(5);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarElemento(elemento)
        );
    }

    @Test
    void listarElementosCorrectamente() {
        dao.guardar(crearElemento(10, "ABC-123"));
        dao.guardar(crearElemento(20, "XYZ-456"));

        assertEquals(2, service.listarElementos().size());
    }

    @Test
    void listarElementosPorServicio() {
        dao.guardar(crearElemento(10, "ABC-123"));
        dao.guardar(crearElemento(20, "XYZ-456"));
        dao.guardar(crearElemento(10, "DEF-789"));

        List<ElementoSupervisado> resultado =
                service.listarElementosPorServicio(10);

        assertEquals(2, resultado.size());
    }

    @Test
    void rechazarBusquedaPorServicioConIdNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.listarElementosPorServicio(null)
        );
    }

    @Test
    void rechazarBusquedaPorServicioConIdInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.listarElementosPorServicio(0)
        );
    }

    @Test
    void buscarElementoPorId() {
        ElementoSupervisado elemento = crearElemento(
                10, "ABC-123"
        );
        dao.guardar(elemento);

        Optional<ElementoSupervisado> resultado =
                service.buscarElementoPorId(elemento.getIdElemento());

        assertTrue(resultado.isPresent());
        assertEquals(
                "ABC-123",
                resultado.get().getIdentificador()
        );
    }

    @Test
    void buscarElementoInexistente() {
        Optional<ElementoSupervisado> resultado =
                service.buscarElementoPorId(999);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void rechazarBusquedaConIdNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.buscarElementoPorId(null)
        );
    }

    @Test
    void rechazarBusquedaConIdInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.buscarElementoPorId(0)
        );
    }

    @Test
    void actualizarElementoCorrectamente() {
        ElementoSupervisado original = crearElemento(
                10, "ABC-123"
        );
        dao.guardar(original);

        ElementoSupervisado actualizado = crearElemento(
                10, "ABC-999"
        );
        actualizado.setIdElemento(original.getIdElemento());

        service.actualizarElemento(actualizado);

        assertEquals(
                "ABC-999",
                service.buscarElementoPorId(
                        original.getIdElemento()
                ).orElseThrow().getIdentificador()
        );
    }

    @Test
    void rechazarActualizacionSinId() {
        ElementoSupervisado elemento = crearElemento(
                10, "ABC-123"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.actualizarElemento(elemento)
        );
    }

    @Test
    void rechazarActualizacionConEstadoNulo() {
        ElementoSupervisado elemento = crearElemento(
                10, "ABC-123"
        );
        elemento.setIdElemento(1);
        elemento.setEstadoActivo(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.actualizarElemento(elemento)
        );
    }

    @Test
    void eliminarElementoCorrectamente() {
        ElementoSupervisado elemento = crearElemento(
                10, "ABC-123"
        );

        dao.guardar(elemento);
        service.eliminarElemento(elemento);

        assertTrue(service.listarElementos().isEmpty());
    }

    @Test
    void rechazarEliminacionSinId() {
        ElementoSupervisado elemento = crearElemento(
                10, "ABC-123"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.eliminarElemento(elemento)
        );
    }

    private ElementoSupervisado crearElemento(
            Integer idServicio,
            String identificador
    ) {
        return new ElementoSupervisado(
                null,
                idServicio,
                TipoElemento.VEHICULO,
                identificador,
                "Vehículo de prueba",
                true
        );
    }

    private static class ElementoDAOFalso
            implements ElementoSupervisadoDAO {

        private final List<ElementoSupervisado> elementos =
                new ArrayList<>();

        private int siguienteId = 1;

        @Override
        public boolean guardar(ElementoSupervisado elemento) {
            if (elemento == null || elemento.getIdElemento() != null) {
                return false;
            }

            elemento.setIdElemento(siguienteId++);
            elementos.add(elemento);
            return true;
        }

        @Override
        public Optional<ElementoSupervisado> obtenerPorId(Integer id) {
            return elementos.stream()
                    .filter(e -> Objects.equals(
                            e.getIdElemento(), id
                    ))
                    .findFirst();
        }

        @Override
        public List<ElementoSupervisado> obtenerTodos() {
            return new ArrayList<>(elementos);
        }

        @Override
        public boolean actualizar(ElementoSupervisado elemento) {
            if (elemento == null || elemento.getIdElemento() == null) {
                return false;
            }

            for (int i = 0; i < elementos.size(); i++) {
                if (Objects.equals(
                        elementos.get(i).getIdElemento(),
                        elemento.getIdElemento()
                )) {
                    elementos.set(i, elemento);
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean eliminar(ElementoSupervisado elemento) {
            if (elemento == null || elemento.getIdElemento() == null) {
                return false;
            }

            return elementos.removeIf(
                    e -> Objects.equals(
                            e.getIdElemento(),
                            elemento.getIdElemento()
                    )
            );
        }

        @Override
        public List<ElementoSupervisado> obtenerPorServicio(
                Integer idServicio
        ) {
            return elementos.stream()
                    .filter(e -> Objects.equals(
                            e.getIdServicio(), idServicio
                    ))
                    .toList();
        }
    }
}
