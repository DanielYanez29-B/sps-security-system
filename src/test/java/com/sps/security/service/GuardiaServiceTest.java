
package com.sps.security.service;

import com.sps.security.dao.GuardiaDAO;
import com.sps.security.model.operative.Guardia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GuardiaServiceTest {

    private GuardiaService service;
    private GuardiaDAOFalso dao;

    @BeforeEach
    void preparar() {
        dao = new GuardiaDAOFalso();
        service = new GuardiaService(dao);
    }

    @Test
    void registrarGuardiaCorrectamente() {

        service.registrarGuardia(
                null,
                "Carlos",
                "Ramirez",
                "Lopez",
                "RALC010101ABC",
                LocalDate.of(2025, 1, 15)
        );

        List<Guardia> guardias = service.listarGuardias();

        assertEquals(1, guardias.size());
        assertEquals("Carlos", guardias.get(0).getNombres());
        assertTrue(guardias.get(0).isEstatusActivo());
    }

    @Test
    void rechazarNombreVacio() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarGuardia(
                        null,
                        "",
                        "Ramirez",
                        "Lopez",
                        "RALC010101ABC",
                        LocalDate.of(2025, 1, 15)
                )
        );

        assertTrue(service.listarGuardias().isEmpty());
    }

    @Test
    void rechazarRfcDuplicado() {

        service.registrarGuardia(
                null,
                "Carlos",
                "Ramirez",
                "Lopez",
                "RALC010101ABC",
                LocalDate.of(2025, 1, 15)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarGuardia(
                        null,
                        "Pedro",
                        "Garcia",
                        "Torres",
                        "RALC010101ABC",
                        LocalDate.of(2025, 2, 10)
                )
        );

        assertEquals(1, service.listarGuardias().size());
    }

    @Test
    void rechazarFechaFutura() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarGuardia(
                        null,
                        "Luis",
                        "Hernandez",
                        null,
                        "HEML020202ABC",
                        LocalDate.now().plusDays(1)
                )
        );
    }

    @Test
    void desactivarGuardiaCorrectamente() {

        service.registrarGuardia(
                null,
                "Carlos",
                "Ramirez",
                null,
                "RALC010101ABC",
                LocalDate.of(2025, 1, 15)
        );

        int id = service.listarGuardias()
                .get(0).getIdGuardia();

        service.desactivarGuardia(id);

        Guardia guardia = service.obtenerGuardiaPorId(id)
                .orElseThrow();

        assertFalse(guardia.isEstatusActivo());
    }

    @Test
    void rechazarUsuarioDuplicado() {

        service.registrarGuardia(
                5,
                "Carlos",
                "Ramirez",
                null,
                "RALC010101ABC",
                LocalDate.of(2025, 1, 15)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrarGuardia(
                        5,
                        "Pedro",
                        "Garcia",
                        null,
                        "GATP020202ABC",
                        LocalDate.of(2025, 2, 15)
                )
        );
    }

    @Test
    void registrarGuardiaSinRfc() {

        service.registrarGuardia(
                null,
                "Juan",
                "Perez",
                "Lopez",
                null,
                LocalDate.of(2025, 3, 10)
        );

        List<Guardia> guardias = service.listarGuardias();

        assertEquals(1, guardias.size());
        assertEquals("Juan", guardias.get(0).getNombres());
        assertNull(guardias.get(0).getRfc());
        assertTrue(guardias.get(0).isEstatusActivo());
    }

    private static class GuardiaDAOFalso implements GuardiaDAO {

        private final List<Guardia> guardias =
                new ArrayList<>();

        private int siguienteId = 1;

        @Override
        public boolean guardar(Guardia guardia) {
            guardia.setIdGuardia(siguienteId++);
            guardias.add(guardia);
            return true;
        }

        @Override
        public Optional<Guardia> obtenerPorId(Integer id) {
            return guardias.stream()
                    .filter(g -> g.getIdGuardia() == id)
                    .findFirst();
        }

        @Override
        public List<Guardia> obtenerTodos() {
            return new ArrayList<>(guardias);
        }

        @Override
        public List<Guardia> obtenerGuardiasActivos() {
            return guardias.stream()
                    .filter(Guardia::isEstatusActivo)
                    .toList();
        }

        @Override
        public boolean actualizar(Guardia guardia) {
            for (int i = 0; i < guardias.size(); i++) {
                if (guardias.get(i).getIdGuardia()
                        == guardia.getIdGuardia()) {
                    guardias.set(i, guardia);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean eliminar(Guardia guardia) {
            return guardias.removeIf(
                    g -> g.getIdGuardia()
                            == guardia.getIdGuardia()
            );
        }

        @Override
        public Optional<Guardia> obtenerPorRfc(String rfc) {
            if (rfc == null) {
                return Optional.empty();
            }

            return guardias.stream()
                    .filter(g -> g.getRfc() != null)
                    .filter(g -> g.getRfc()
                            .equalsIgnoreCase(rfc))
                    .findFirst();
        }

        @Override
        public Optional<Guardia> obtenerPorIdUsuario(
                Integer idUsuario) {

            if (idUsuario == null) {
                return Optional.empty();
            }

            return guardias.stream()
                    .filter(g -> idUsuario.equals(
                            g.getIdUsuario()))
                    .findFirst();
        }
    }
}
