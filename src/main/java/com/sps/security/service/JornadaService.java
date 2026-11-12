
package com.sps.security.service;

import com.sps.security.dao.JornadaDAO;
import com.sps.security.dao.impl.JornadaDAOImpl;
import com.sps.security.model.operative.Jornada;

import java.time.LocalTime;
import java.util.List;

public class JornadaService {

    private final JornadaDAO jornadaDAO;

    public JornadaService() {
        this.jornadaDAO = new JornadaDAOImpl();
    }

    // Constructor para pruebas JUnit
    public JornadaService(JornadaDAO jornadaDAO) {
        if (jornadaDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de jornadas es obligatorio."
            );
        }
        this.jornadaDAO = jornadaDAO;
    }


    public void registrarJornada(
            String nombre,
            LocalTime horaInicio,
            LocalTime horaFin) {

        validarDatos(nombre, horaInicio, horaFin);

        String nombreLimpio = nombre.trim();

        if (jornadaDAO.obtenerPorNombre(nombreLimpio).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe una jornada registrada con ese nombre."
            );
        }

        Jornada jornada = new Jornada();
        jornada.setNombre(nombreLimpio);
        jornada.setHoraInicio(horaInicio);
        jornada.setHoraFin(horaFin);
        jornada.setActivo(true);

        if (!jornadaDAO.guardar(jornada)) {
            throw new RuntimeException(
                    "No se pudo registrar la jornada en la base de datos."
            );
        }
    }


    public List<Jornada> listarJornadas() {
        return jornadaDAO.obtenerTodos();
    }


    public Jornada buscarJornadaPorId(int idJornada) {

        return jornadaDAO.obtenerPorId(idJornada)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una jornada con el ID: " + idJornada
                ));
    }



    public void actualizarJornada(
            int idJornada,
            String nombre,
            LocalTime horaInicio,
            LocalTime horaFin) {

        validarDatos(nombre, horaInicio, horaFin);

        Jornada jornada = buscarJornadaPorId(idJornada);

        String nombreLimpio = nombre.trim();

        jornadaDAO.obtenerPorNombre(nombreLimpio)
                .ifPresent(existente -> {
                    if (existente.getIdJornada() != idJornada) {
                        throw new IllegalArgumentException(
                                "Ya existe otra jornada con ese nombre."
                        );
                    }
                });

        jornada.setNombre(nombreLimpio);
        jornada.setHoraInicio(horaInicio);
        jornada.setHoraFin(horaFin);

        if (!jornadaDAO.actualizar(jornada)) {
            throw new RuntimeException(
                    "No se pudo actualizar la jornada."
            );
        }
    }


    public void desactivarJornada(int idJornada) {

        Jornada jornada = buscarJornadaPorId(idJornada);

        if (!jornada.isActivo()) {
            throw new IllegalArgumentException(
                    "La jornada ya se encuentra desactivada."
            );
        }

        jornada.setActivo(false);

        if (!jornadaDAO.actualizar(jornada)) {
            throw new RuntimeException(
                    "No se pudo desactivar la jornada."
            );
        }
    }




    public void activarJornada(int idJornada) {

        Jornada jornada = buscarJornadaPorId(idJornada);

        if (jornada.isActivo()) {
            throw new IllegalArgumentException(
                    "La jornada ya se encuentra activa."
            );
        }

        jornada.setActivo(true);

        if (!jornadaDAO.actualizar(jornada)) {
            throw new RuntimeException(
                    "No se pudo activar la jornada."
            );
        }
    }


    private void validarDatos(
            String nombre,
            LocalTime horaInicio,
            LocalTime horaFin) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la jornada es obligatorio."
            );
        }

        if (horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException(
                    "Las horas de inicio y fin son obligatorias."
            );
        }

        if (horaInicio.equals(horaFin)) {
            throw new IllegalArgumentException(
                    "La hora de inicio debe ser distinta de la hora de fin."
            );
        }

    }
}
