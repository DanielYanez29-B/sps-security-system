package com.sps.security.service;

import com.sps.security.dao.DispositivoDAO;
import com.sps.security.dao.impl.DispositivoDAOImpl;
import com.sps.security.model.telemetry.Dispositivo;

import java.util.Optional;

public class DispositivoService {

    private static final String MAC_REGEX =
            "^[0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2}){5}$";

    private final DispositivoDAO dispositivoDAO;

    public DispositivoService() {
        this.dispositivoDAO = new DispositivoDAOImpl();
    }

    public DispositivoService(DispositivoDAO dispositivoDAO) {
        if (dispositivoDAO == null) {
            throw new IllegalArgumentException("El DAO no puede ser nulo.");
        }
        this.dispositivoDAO = dispositivoDAO;
    }

    public void registrar(Dispositivo dispositivo) {
        validarDatos(dispositivo);

        Optional<Dispositivo> existente =
                dispositivoDAO.obtenerPorNumSerie(dispositivo.getNumSerie());

        if (existente.isPresent()) {
            throw new IllegalArgumentException(
                    "El número de serie ya está registrado."
            );
        }

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

        Optional<Dispositivo> existente =
                dispositivoDAO.obtenerPorNumSerie(dispositivo.getNumSerie());

        if (existente.isPresent()
                && !dispositivo.getIdDispositivo().equals(
                existente.get().getIdDispositivo())) {
            throw new IllegalArgumentException(
                    "El número de serie ya pertenece a otro dispositivo."
            );
        }

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
