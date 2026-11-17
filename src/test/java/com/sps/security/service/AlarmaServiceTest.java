package com.sps.security.service;

import com.sps.security.dao.AlarmaDAO;
import com.sps.security.dao.DispositivoDAO;
import com.sps.security.dao.NotificacionDAO;
import com.sps.security.dao.UsuarioDAO;
import com.sps.security.model.enums.EstadoAlarma;
import com.sps.security.model.enums.NivelPrioridad;
import com.sps.security.model.telemetry.Alarma;
import com.sps.security.model.telemetry.Dispositivo;
import com.sps.security.model.telemetry.Notificacion;
import com.sps.security.model.users.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AlarmaServiceTest {
    private FakeAlarmaDAO alarmas;
    private FakeDispositivoDAO dispositivos;
    private FakeUsuarioDAO usuarios;
    private FakeNotificacionDAO notificaciones;
    private AlarmaService service;

    @BeforeEach
    void setUp() {
        alarmas = new FakeAlarmaDAO();
        dispositivos = new FakeDispositivoDAO();
        usuarios = new FakeUsuarioDAO();
        notificaciones = new FakeNotificacionDAO();
        dispositivos.items.add(dispositivo(7, true));
        usuarios.items.add(usuario(1, true, 1));
        usuarios.items.add(usuario(2, true, 3));
        usuarios.items.add(usuario(3, false, 2));
        service = new AlarmaService(alarmas, dispositivos, usuarios,
                new NotificacionService(notificaciones));
    }

    @Test
    void registrarCreaPendienteYNotificaSoloRolesPermitidos() {
        Alarma alarma = service.registrar(7, "incendio",
                NivelPrioridad.CRITICA, "Humo detectado");

        assertEquals(EstadoAlarma.PENDIENTE, alarma.getEstado());
        assertEquals(1, alarmas.items.size());
        assertEquals(1, notificaciones.items.size());
        assertEquals(1, notificaciones.items.get(0).getIdUsuario());
    }

    @Test
    void rechazaDispositivoInactivo() {
        dispositivos.items.set(0, dispositivo(7, false));

        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(7, "INCENDIO",
                        NivelPrioridad.ALTA, null));
    }

    @Test
    void rechazaTipoInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registrar(7, "ROBO",
                        NivelPrioridad.ALTA, null));
    }

    @Test
    void soloPermiteCambiarAlarmaPendiente() {
        Alarma alarma = service.registrar(7, "SISMO",
                NivelPrioridad.MEDIA, null);

        service.cambiarEstado(alarma.getIdAlarma(), EstadoAlarma.ATENDIDA);
        assertEquals(EstadoAlarma.ATENDIDA, alarmas.items.get(0).getEstado());
        assertThrows(IllegalArgumentException.class,
                () -> service.cambiarEstado(alarma.getIdAlarma(),
                        EstadoAlarma.FALSA_ALARMA));
    }

    @Test
    void rechazaRangoInvertido() {
        assertThrows(IllegalArgumentException.class,
                () -> service.filtrar(
                        java.time.LocalDateTime.now(),
                        java.time.LocalDateTime.now().minusMinutes(1),
                        null, null, null));
    }

    private Dispositivo dispositivo(int id, boolean activo) {
        return new Dispositivo(id, 1, "ESP-" + id, null, null,
                "ESP32", LocalDate.now(), activo);
    }

    private Usuario usuario(int id, boolean activo, int rol) {
        return new Usuario(id, rol, "u" + id, "u" + id + "@test.local",
                "hash", activo);
    }

    private static class FakeAlarmaDAO implements AlarmaDAO {
        private final List<Alarma> items = new ArrayList<>();
        public boolean guardar(Alarma a) { a.setIdAlarma(items.size() + 1); items.add(a); return true; }
        public Optional<Alarma> obtenerPorId(Integer id) { return items.stream().filter(a -> id.equals(a.getIdAlarma())).findFirst(); }
        public List<Alarma> obtenerTodos() { return new ArrayList<>(items); }
        public boolean actualizar(Alarma a) { return true; }
        public boolean eliminar(Alarma a) { return items.remove(a); }
        public List<Alarma> obtenerPorDispositivo(Integer id) { return items; }
        public List<Alarma> obtenerFiltradas(java.time.LocalDateTime d, java.time.LocalDateTime h, Integer i, NivelPrioridad p, EstadoAlarma e) { return items; }
    }

    private static class FakeDispositivoDAO implements DispositivoDAO {
        private final List<Dispositivo> items = new ArrayList<>();
        public boolean guardar(Dispositivo d) { return items.add(d); }
        public Optional<Dispositivo> obtenerPorId(Integer id) { return items.stream().filter(d -> id.equals(d.getIdDispositivo())).findFirst(); }
        public List<Dispositivo> obtenerTodos() { return new ArrayList<>(items); }
        public boolean actualizar(Dispositivo d) { return true; }
        public boolean eliminar(Dispositivo d) { return items.remove(d); }
        public Optional<Dispositivo> obtenerPorNumSerie(String n) { return items.stream().filter(d -> n.equals(d.getNumSerie())).findFirst(); }
    }

    private static class FakeUsuarioDAO implements UsuarioDAO {
        private final List<Usuario> items = new ArrayList<>();
        public boolean guardar(Usuario u) { return items.add(u); }
        public Optional<Usuario> obtenerPorId(Integer id) { return items.stream().filter(u -> id.equals(u.getIdUsuario())).findFirst(); }
        public List<Usuario> obtenerTodos() { return new ArrayList<>(items); }
        public boolean actualizar(Usuario u) { return true; }
        public boolean eliminar(Usuario u) { return items.remove(u); }
        public Optional<Usuario> buscarPorEmail(String e) { return items.stream().filter(u -> e.equals(u.getEmail())).findFirst(); }
        public List<Usuario> obtenerUsuariosActivosEInactivos(String e) { return items; }
    }

    private static class FakeNotificacionDAO implements NotificacionDAO {
        private final List<Notificacion> items = new ArrayList<>();
        public boolean guardar(Notificacion n) { n.setIdNotificacion(items.size() + 1); items.add(n); return true; }
        public Optional<Notificacion> obtenerPorId(Integer id) { return items.stream().filter(n -> id.equals(n.getIdNotificacion())).findFirst(); }
        public List<Notificacion> obtenerTodos() { return new ArrayList<>(items); }
        public boolean actualizar(Notificacion n) { return true; }
        public boolean eliminar(Notificacion n) { return items.remove(n); }
        public List<Notificacion> obtenerPorAlarma(Integer id) { return items; }
        public List<Notificacion> obtenerPorUsuario(Integer id) { return items; }
        public List<Notificacion> obtenerNoLeidasPorUsuario(Integer id) { return items; }
        public boolean marcarComoLeida(Integer id) { return true; }
        public int marcarTodasComoLeidas(Integer id) { return items.size(); }
    }
}
