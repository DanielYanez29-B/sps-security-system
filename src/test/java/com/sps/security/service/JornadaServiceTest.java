
package com.sps.security.service;

import com.sps.security.dao.JornadaDAO;
import com.sps.security.model.operative.Jornada;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JornadaServiceTest {

    private JornadaService service;
    private JornadaDAOFalso dao;

    @BeforeEach
    void preparar() {
        dao = new JornadaDAOFalso();
        service = new JornadaService(dao);
    }


    @Test
    void registrarJornadaValida() {

        service.registrarJornada(
                "Matutino",
                LocalTime.of(6, 0),
                LocalTime.of(14, 0)
        );

        List<Jornada> jornadas = service.listarJornadas();

        assertEquals(1, jornadas.size());
        assertEquals("Matutino", jornadas.get(0).getNombre());
        assertTrue(jornadas.get(0).isActivo());
    }


    @Test
    void rechazarHorariosIguales() {

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarJornada(
                        "Jornada incorrecta",
                        LocalTime.of(8, 0),
                        LocalTime.of(8, 0)
                )
        );

        assertTrue(error.getMessage().contains("distinta"));
        assertTrue(service.listarJornadas().isEmpty());
    }

    @Test
    void permitirJornadaNocturna() {

        service.registrarJornada(
                "Nocturno",
                LocalTime.of(22, 0),
                LocalTime.of(6, 0)
        );

        Jornada jornada = service.listarJornadas().get(0);

        assertEquals(LocalTime.of(22, 0),
                jornada.getHoraInicio());

        assertEquals(LocalTime.of(6, 0),
                jornada.getHoraFin());

        assertTrue(jornada.isActivo());
    }

    @Test
    void rechazarNombreDuplicado() {

        service.registrarJornada(
                "Matutino",
                LocalTime.of(6, 0),
                LocalTime.of(14, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarJornada(
                        "Matutino",
                        LocalTime.of(7, 0),
                        LocalTime.of(15, 0)
                )
        );

        assertEquals(1, service.listarJornadas().size());
    }

    @Test
    void desactivarJornadaCorrectamente() {

        service.registrarJornada(
                "Vespertino",
                LocalTime.of(14, 0),
                LocalTime.of(22, 0)
        );

        int id = service.listarJornadas()
                .get(0).getIdJornada();

        service.desactivarJornada(id);

        Jornada jornada = service.buscarJornadaPorId(id);

        assertFalse(jornada.isActivo());
    }

    private static class JornadaDAOFalso implements JornadaDAO {

        private final List<Jornada> jornadas = new ArrayList<>();
        private int siguienteId = 1;

        @Override
        public boolean guardar(Jornada jornada) {
            jornada.setIdJornada(siguienteId++);
            jornadas.add(jornada);
            return true;
        }

        @Override
        public Optional<Jornada> obtenerPorId(Integer id) {
            return jornadas.stream()
                    .filter(j -> j.getIdJornada() == id)
                    .findFirst();
        }

        @Override
        public List<Jornada> obtenerTodos() {
            return new ArrayList<>(jornadas);
        }

        @Override
        public boolean actualizar(Jornada jornada) {
            for (int i = 0; i < jornadas.size(); i++) {
                if (jornadas.get(i).getIdJornada()
                        == jornada.getIdJornada()) {
                    jornadas.set(i, jornada);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean eliminar(Jornada jornada) {
            return jornadas.removeIf(
                    j -> j.getIdJornada()
                            == jornada.getIdJornada()
            );
        }

        @Override
        public Optional<Jornada> obtenerPorNombre(String nombre) {
            return jornadas.stream()
                    .filter(j -> j.getNombre()
                            .equalsIgnoreCase(nombre))
                    .findFirst();
        }
    }
}
