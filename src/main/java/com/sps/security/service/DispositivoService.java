package com.sps.security.service;

import com.sps.security.dao.DispositivoDAO;
import com.sps.security.dao.impl.DispositivoDAOImpl;
import com.sps.security.dao.ServicioDAO;
import com.sps.security.dao.impl.ServicioDAOImpl;
import com.sps.security.model.telemetry.Dispositivo;

import java.util.Optional;

public class DispositivoService {

    private static final String MAC_REGEX =
            "^[0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2}){5}$";

    private final DispositivoDAO dispositivoDAO;
    private final ServicioDAO servicioDAO;

    public DispositivoService() {
        this.dispositivoDAO = new DispositivoDAOImpl();
        this.servicioDAO = new ServicioDAOImpl();
    }

    public DispositivoService(DispositivoDAO dispositivoDAO) {
        this(dispositivoDAO, null);
    }

    public DispositivoService(DispositivoDAO dispositivoDAO, ServicioDAO servicioDAO) {
        if (dispositivoDAO == null) {
            throw new IllegalArgumentException("El DAO no puede ser nulo.");
        }
        this.dispositivoDAO = dispositivoDAO;
        this.servicioDAO = servicioDAO;
    }

    public void registrar(Dispositivo dispositivo) {
        validarDatos(dispositivo);
        validarServicio(dispositivo.getIdServicio());

        Optional<Dispositivo> existente =
                dispositivoDAO.obtenerPorNumSerie(dispositivo.getNumSerie());

        if (existente.isPresent()) {
            throw new IllegalArgumentException(
                    "El número de serie ya está registrado."
            );
        }
        validarMacDuplicada(dispositivo.getMacAddress(), null);

        prepararParaGuardar(dispositivo);

        if (!dispositivoDAO.guardar(dispositivo)) {
            throw new IllegalStateException(
                    "No se pudo registrar el dispositivo."
            );
        }
    }

    public void actualizar(Dispositivo dispositivo) {
        if (dispositivo == null
                || dispositivo.getIdDispositivo() == null
                || dispositivo.getIdDispositivo() <= 0) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un dispositivo válido para actualizar."
            );
        }

        validarDatos(dispositivo);
        validarServicio(dispositivo.getIdServicio());

        Optional<Dispositivo> existente =
                dispositivoDAO.obtenerPorNumSerie(dispositivo.getNumSerie());

        if (existente.isPresent()
                && !dispositivo.getIdDispositivo().equals(
                existente.get().getIdDispositivo())) {
            throw new IllegalArgumentException(
                    "El número de serie ya pertenece a otro dispositivo."
            );
        }
        validarMacDuplicada(dispositivo.getMacAddress(), dispositivo.getIdDispositivo());

        prepararParaGuardar(dispositivo);

        if (!dispositivoDAO.actualizar(dispositivo)) {
            throw new IllegalStateException(
                    "No se pudo actualizar el dispositivo."
            );
        }
    }

    public void desactivar(Integer idDispositivo) {
        if (idDispositivo == null || idDispositivo <= 0) {
            throw new IllegalArgumentException(
                    "Debe proporcionar un dispositivo válido."
            );
        }

        Dispositivo dispositivo = dispositivoDAO
                .obtenerPorId(idDispositivo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El dispositivo no existe."
                ));

        dispositivo.setEstadoActividad(false);
        if (!dispositivoDAO.actualizar(dispositivo)) {
            throw new IllegalStateException(
                    "No se pudo desactivar el dispositivo."
            );
        }
    }

    private void validarDatos(Dispositivo dispositivo) {
        if (dispositivo == null) {
            throw new IllegalArgumentException(
                    "El dispositivo no puede ser nulo."
            );
        }

        if (dispositivo.getIdServicio() == null
                || dispositivo.getIdServicio() <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un servicio válido."
            );
        }

        if (dispositivo.getNumSerie() == null
                || dispositivo.getNumSerie().isBlank()) {
            throw new IllegalArgumentException(
                    "El número de serie es obligatorio."
            );
        }

        String mac = dispositivo.getMacAddress();
        if (mac != null && !mac.isBlank()
                && !mac.matches(MAC_REGEX)) {
            throw new IllegalArgumentException(
                    "La MAC debe tener formato AA:BB:CC:DD:EE:FF."
            );
        }

        String ip = dispositivo.getIpAsignada();
        if (ip != null && !ip.isBlank() && !esIpValida(ip)) {
            throw new IllegalArgumentException(
                    "La dirección IP no es válida."
            );
        }
    }

    private void prepararParaGuardar(Dispositivo dispositivo) {
        if (dispositivo.getMacAddress() != null
                && !dispositivo.getMacAddress().isBlank()) {
            dispositivo.setMacAddress(
                    dispositivo.getMacAddress().toUpperCase()
            );
        }

        if (dispositivo.getFabricante() == null
                || dispositivo.getFabricante().isBlank()) {
            dispositivo.setFabricante("ESP32");
        }

        if (dispositivo.getEstadoActividad() == null) {
            dispositivo.setEstadoActividad(true);
        }
    }

    private void validarServicio(Integer idServicio) {
        if (servicioDAO == null) return;
        var servicio = servicioDAO.obtenerPorId(idServicio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El servicio seleccionado no existe."));
        if (servicio.getEstado() != null
                && !"ACTIVO".equalsIgnoreCase(servicio.getEstado())) {
            throw new IllegalArgumentException(
                    "El servicio seleccionado está inactivo.");
        }
    }

    private void validarMacDuplicada(String mac, Integer idActual) {
        if (mac == null || mac.isBlank()) return;
        String normalizada = mac.toUpperCase();
        boolean duplicada = dispositivoDAO.obtenerTodos().stream()
                .filter(d -> d.getMacAddress() != null
                        && d.getMacAddress().equalsIgnoreCase(normalizada))
                .anyMatch(d -> idActual == null
                        || !idActual.equals(d.getIdDispositivo()));
        if (duplicada) {
            throw new IllegalArgumentException(
                    "La dirección MAC ya está registrada.");
        }
    }

    private boolean esIpValida(String ip) {
        String[] partes = ip.split("\\.", -1);
        if (partes.length != 4) {
            return false;
        }

        for (String parte : partes) {
            try {
                int octeto = Integer.parseInt(parte);
                if (octeto < 0 || octeto > 255) {
                    return false;
                }
            } catch (NumberFormatException exception) {
                return false;
            }
        }

        return true;
    }
}
