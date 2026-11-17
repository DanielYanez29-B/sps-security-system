
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
                1, 10, "ABC-123"
        );

        service.registrarElemento(elemento);

        assertEquals(1, service.listarElementos().size());
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
                1, null, "ABC-123"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarElemento(elemento)
        );
    }

    @Test
    void rechazarTipoElementoNulo() {
        ElementoSupervisado elemento = new ElementoSupervisado(
                1, 10, null, "ABC-123",
                "Vehículo supervisado", true
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarElemento(elemento)
        );
    }

    @Test
    void rechazarIdentificadorVacio() {
        ElementoSupervisado elemento = crearElemento(
                1, 10, ""
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarElemento(elemento)
        );
    }

    @Test
    void listarElementosCorrectamente() {
        dao.guardar(crearElemento(1, 10, "ABC-123"));
        dao.guardar(crearElemento(2, 20, "XYZ-456"));

        assertEquals(2, service.listarElementos().size());
    }

    @Test
    void listarElementosPorServicio() {
        dao.guardar(crearElemento(1, 10, "ABC-123"));
        dao.guardar(crearElemento(2, 20, "XYZ-456"));
        dao.guardar(crearElemento(3, 10, "DEF-789"));

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
    void buscarElementoPorId() {
        dao.guardar(crearElemento(1, 10, "ABC-123"));

        Optional<ElementoSupervisado> resultado =
                service.buscarElementoPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("ABC-123", resultado.get().getIdentificador());
    }

    @Test
    void actualizarElementoCorrectamente() {
        dao.guardar(crearElemento(1, 10, "ABC-123"));

        ElementoSupervisado actualizado = crearElemento(
                1, 10, "ABC-999"
        );

        service.actualizarElemento(actualizado);

        assertEquals(
                "ABC-999",
                service.buscarElementoPorId(1)
                        .get()
                        .getIdentificador()
        );
    }

    @Test
    void eliminarElementoCorrectamente() {
        ElementoSupervisado elemento = crearElemento(
                1, 10, "ABC-123"
        );

        dao.guardar(elemento);
        service.eliminarElemento(elemento);

        assertTrue(service.listarElementos().isEmpty());
    }

    private ElementoSupervisado crearElemento(
            Integer id,
            Integer idServicio,
            String identificador
    ) {
        return new ElementoSupervisado(
                id,
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

        @Override
        public boolean guardar(ElementoSupervisado elemento) {
            elementos.add(elemento);
            return true;
        }

        @Override
        public Optional<ElementoSupervisado> obtenerPorId(Integer id) {
            return elementos.stream()
                    .filter(e -> Objects.equals(e.getIdElemento(), id))
                    .findFirst();
        }

        @Override
        public List<ElementoSupervisado> obtenerTodos() {
            return new ArrayList<>(elementos);
        }

        @Override
        public boolean actualizar(ElementoSupervisado elemento) {
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
