package com.sps.security.service;

import com.sps.security.dao.JornadaDAO;
import com.sps.security.dao.impl.JornadaDAOImpl;
import com.sps.security.model.Jornada;

import java.time.LocalTime;
import java.util.List;

public class JornadaService {
    private final JornadaDAO jornadaDAO = new JornadaDAOImpl();

    public void registrarJornada(String nombre, LocalTime horaInicio, LocalTime horaFin) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la jornada es obligatorio.");
        }
        if (horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException("Las horas de inicio y fin son obligatorias.");
        }

        if (horaInicio.equals(horaFin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser distinta de la hora de fin.");
        }

        if (jornadaDAO.obtenerPorNombre(nombre.trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una jornada registrada con ese nombre.");
        }

        Jornada jornada = new Jornada();
        jornada.setNombre(nombre.trim());
        jornada.setHoraInicio(horaInicio);
        jornada.setHoraFin(horaFin);
        jornada.setActivo(true);

        boolean exito = jornadaDAO.guardar(jornada);
        if (!exito) {
            throw new RuntimeException("No se pudo guardar la jornada en la base de datos.");
        }
    }

    public List<Jornada> listarJornadas() {
        return jornadaDAO.obtenerTodos();
    }
}