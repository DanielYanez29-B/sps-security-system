package com.sps.security.service;

import com.sps.security.dao.DispositivoDAO;
import com.sps.security.model.telemetry.Dispositivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispositivoServiceTest {

    private FakeDispositivoDAO dao;
    private DispositivoService service;

    @BeforeEach
    void setUp() {
        dao = new FakeDispositivoDAO();
        service = new DispositivoService(dao);
    }

    @Test
    void registrarDispositivoValido() {
        Dispositivo dispositivo = dispositivoBase();

        service.registrar(dispositivo);

        assertEquals(1, dispositivo.getIdDispositivo());
        assertEquals(1, dao.dispositivos.size());
    }

    @Test
    void rechazarNumeroSerieVacio() {
        Dispositivo dispositivo = dispositivoBase();
        dispositivo.setNumSerie(" ");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrar(dispositivo)
        );
    }

    @Test
    void rechazarMacInvalida() {
        Dispositivo dispositivo = dispositivoBase();
        dispositivo.setMacAddress("00:11:22");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrar(dispositivo)
        );
    }

    @Test
    void rechazarIpInvalida() {
        Dispositivo dispositivo = dispositivoBase();
        dispositivo.setIpAsignada("192.168.1.256");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrar(dispositivo)
        );
    }

    @Test
    void rechazarNumeroSerieDuplicado() {
        dao.dispositivos.add(dispositivoBaseConId(5));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registrar(dispositivoBase())
        );
    }

    @Test
    void asignarFabricanteESP32() {
        Dispositivo dispositivo = dispositivoBase();
        dispositivo.setFabricante(" ");

        service.registrar(dispositivo);

        assertEquals("ESP32", dispositivo.getFabricante());
    }

    @Test
    void normalizarMacAMayusculas() {
        Dispositivo dispositivo = dispositivoBase();
        dispositivo.setMacAddress("aa:bb:cc:dd:ee:ff");

        service.registrar(dispositivo);

        assertEquals("AA:BB:CC:DD:EE:FF", dispositivo.getMacAddress());
    }

    @Test
    void activarPorDefectoElDispositivo() {
        Dispositivo dispositivo = dispositivoBase();
        dispositivo.setEstadoActividad(null);

        service.registrar(dispositivo);

        assertTrue(dispositivo.getEstadoActividad());
    }

    @Test
    void desactivarActualizaEstado() {
        Dispositivo dispositivo = dispositivoBaseConId(1);
        dao.dispositivos.add(dispositivo);

        service.desactivar(1);

        assertFalse(dispositivo.getEstadoActividad());
        assertTrue(dao.actualizarFueLlamado);
    }

    private Dispositivo dispositivoBase() {
        return new Dispositivo(
                null,
                1,
                "ESP-001",
                null,
                null,
                null,
                LocalDate.of(2026, 1, 1),
                null
        );
    }

    private Dispositivo dispositivoBaseConId(int id) {
        Dispositivo dispositivo = dispositivoBase();
        dispositivo.setIdDispositivo(id);
        return dispositivo;
    }

    private static class FakeDispositivoDAO implements DispositivoDAO {
        private final List<Dispositivo> dispositivos = new ArrayList<>();
        private boolean actualizarFueLlamado;

        @Override
        public boolean guardar(Dispositivo dispositivo) {
            dispositivo.setIdDispositivo(dispositivos.size() + 1);
            dispositivos.add(dispositivo);
            return true;
        }

        @Override
        public Optional<Dispositivo> obtenerPorId(Integer id) {
            return dispositivos.stream()
                    .filter(dispositivo -> id.equals(
                            dispositivo.getIdDispositivo()))
                    .findFirst();
        }

        @Override
        public List<Dispositivo> obtenerTodos() {
            return new ArrayList<>(dispositivos);
        }

        @Override
        public boolean actualizar(Dispositivo dispositivo) {
            actualizarFueLlamado = true;
            return dispositivos.stream()
                    .anyMatch(actual -> dispositivo.getIdDispositivo().equals(
                            actual.getIdDispositivo()));
        }

        @Override
        public boolean eliminar(Dispositivo dispositivo) {
            return dispositivos.remove(dispositivo);
        }

        @Override
        public Optional<Dispositivo> obtenerPorNumSerie(String numSerie) {
            return dispositivos.stream()
                    .filter(dispositivo -> numSerie.equals(
                            dispositivo.getNumSerie()))
                    .findFirst();
        }
    }
}
