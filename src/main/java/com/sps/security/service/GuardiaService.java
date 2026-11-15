
package com.sps.security.service;

import com.sps.security.dao.GuardiaDAO;
import com.sps.security.dao.impl.GuardiaDAOImpl;
import com.sps.security.model.operative.Guardia;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class GuardiaService {

    private final GuardiaDAO guardiaDAO;

    public GuardiaService() {
        this(new GuardiaDAOImpl());
    }

    public GuardiaService(GuardiaDAO guardiaDAO) {
        this.guardiaDAO = Objects.requireNonNull(guardiaDAO);
    }

    public void registrarGuardia(
            Integer idUsuario,
            String nombres,
            String apellidoPaterno,
            String apellidoMaterno,
            String rfc,
            LocalDate fechaIngreso) {

        validarDatos(
                nombres,
                apellidoPaterno,
                rfc,
                fechaIngreso
        );

        String rfcNormalizado = normalizarRfc(rfc);

        validarDuplicados(rfcNormalizado, idUsuario, null);

        Guardia guardia = new Guardia();

        guardia.setIdUsuario(idUsuario);
        guardia.setNombres(nombres.trim());
        guardia.setApellidoPaterno(apellidoPaterno.trim());
        guardia.setApellidoMaterno(normalizarOpcional(apellidoMaterno));
        guardia.setRfc(rfcNormalizado);
        guardia.setFechaIngreso(fechaIngreso);
        guardia.setEstatusActivo(true);

        if (!guardiaDAO.guardar(guardia)) {
            throw new IllegalStateException(
                    "No se pudo registrar el guardia."
            );
        }
    }

    public void actualizarGuardia(
            Integer idGuardia,
            Integer idUsuario,
            String nombres,
            String apellidoPaterno,
            String apellidoMaterno,
            String rfc,
            LocalDate fechaIngreso) {

        Guardia guardia = buscarGuardiaExistente(idGuardia);

        validarDatos(
                nombres,
                apellidoPaterno,
                rfc,
                fechaIngreso
        );

        String rfcNormalizado = normalizarRfc(rfc);

        validarDuplicados(
                rfcNormalizado,
                idUsuario,
                idGuardia
        );

        guardia.setIdUsuario(idUsuario);
        guardia.setNombres(nombres.trim());
        guardia.setApellidoPaterno(apellidoPaterno.trim());
        guardia.setApellidoMaterno(normalizarOpcional(apellidoMaterno));
        guardia.setRfc(rfcNormalizado);
        guardia.setFechaIngreso(fechaIngreso);

        if (!guardiaDAO.actualizar(guardia)) {
            throw new IllegalStateException(
                    "No se pudo actualizar el guardia."
            );
        }
    }

    public void desactivarGuardia(Integer idGuardia) {

        Guardia guardia = buscarGuardiaExistente(idGuardia);

        if (!guardia.isEstatusActivo()) {
            throw new IllegalArgumentException(
                    "El guardia ya está desactivado."
            );
        }

        guardia.setEstatusActivo(false);

        if (!guardiaDAO.actualizar(guardia)) {
            throw new IllegalStateException(
                    "No se pudo desactivar el guardia."
            );
        }
    }

    public void activarGuardia(Integer idGuardia) {

        Guardia guardia = buscarGuardiaExistente(idGuardia);

        if (guardia.isEstatusActivo()) {
            throw new IllegalArgumentException(
                    "El guardia ya está activo."
            );
        }

        guardia.setEstatusActivo(true);

        if (!guardiaDAO.actualizar(guardia)) {
            throw new IllegalStateException(
                    "No se pudo activar el guardia."
            );
        }
    }

    public List<Guardia> listarGuardias() {
        return guardiaDAO.obtenerTodos();
    }

    public List<Guardia> listarGuardiasActivos() {
        return guardiaDAO.obtenerGuardiasActivos();
    }

    public Optional<Guardia> obtenerGuardiaPorId(Integer idGuardia) {

        if (idGuardia == null || idGuardia <= 0) {
            throw new IllegalArgumentException(
                    "El ID del guardia debe ser válido."
            );
        }

        return guardiaDAO.obtenerPorId(idGuardia);
    }

    private Guardia buscarGuardiaExistente(Integer idGuardia) {

        if (idGuardia == null || idGuardia <= 0) {
            throw new IllegalArgumentException(
                    "El ID del guardia debe ser válido."
            );
        }

        return guardiaDAO.obtenerPorId(idGuardia)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un guardia con ID " + idGuardia
                ));
    }

    private void validarDatos(
            String nombres,
            String apellidoPaterno,
            String rfc,
            LocalDate fechaIngreso) {

        if (esVacio(nombres)) {
            throw new IllegalArgumentException(
                    "El nombre del guardia es obligatorio."
            );
        }

        if (esVacio(apellidoPaterno)) {
            throw new IllegalArgumentException(
                    "El apellido paterno es obligatorio."
            );
        }

        if (!esVacio(rfc)) {

            String rfcNormalizado = normalizarRfc(rfc);

            if (!rfcNormalizado.matches(
                    "^[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}$"
            )) {
                throw new IllegalArgumentException(
                        "El RFC debe contener 12 o 13 caracteres "
                                + "con un formato válido."
                );
            }
        }

        if (fechaIngreso == null) {
            throw new IllegalArgumentException(
                    "La fecha de ingreso es obligatoria."
            );
        }

        if (fechaIngreso.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de ingreso no puede ser futura."
            );
        }
    }

    private void validarDuplicados(
            String rfc,
            Integer idUsuario,
            Integer idGuardiaActual) {

        if (rfc != null) {

            Optional<Guardia> guardiaConRfc =
                    guardiaDAO.obtenerPorRfc(rfc);

            if (guardiaConRfc.isPresent()
                    && esOtroGuardia(
                    guardiaConRfc.get(),
                    idGuardiaActual
            )) {

                throw new IllegalArgumentException(
                        "Ya existe un guardia registrado con ese RFC."
                );
            }
        }

        if (idUsuario != null) {

            if (idUsuario <= 0) {
                throw new IllegalArgumentException(
                        "El ID de usuario debe ser válido."
                );
            }

            Optional<Guardia> guardiaConUsuario =
                    guardiaDAO.obtenerPorIdUsuario(idUsuario);

            if (guardiaConUsuario.isPresent()
                    && esOtroGuardia(
                    guardiaConUsuario.get(),
                    idGuardiaActual
            )) {

                throw new IllegalArgumentException(
                        "El usuario ya está vinculado a otro guardia."
                );
            }
        }
    }

    private boolean esOtroGuardia(
            Guardia guardia,
            Integer idGuardiaActual) {

        return idGuardiaActual == null
                || guardia.getIdGuardia() != idGuardiaActual;
    }

    private String normalizarRfc(String rfc) {

        if (esVacio(rfc)) {
            return null;
        }

        return rfc.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizarOpcional(String texto) {
        return esVacio(texto) ? null : texto.trim();
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
