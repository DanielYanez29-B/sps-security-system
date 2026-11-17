
package com.sps.security.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class ValidadorTraslapesTest {

    private final LocalDate fecha = LocalDate.of(2026, 11, 15);

    @Test
    void rechazarHorariosSuperpuestos() {
        assertTrue(ValidadorTraslapes.seTraslapan(
                fecha,
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                fecha,
                LocalTime.of(12, 0),
                LocalTime.of(20, 0)
        ));
    }

    @Test
    void permitirHorariosConsecutivos() {
        assertFalse(ValidadorTraslapes.seTraslapan(
                fecha,
                LocalTime.of(8, 0),
                LocalTime.of(14, 0),
                fecha,
                LocalTime.of(14, 0),
                LocalTime.of(22, 0)
        ));
    }

    @Test
    void detectarTraslapeNocturno() {
        assertTrue(ValidadorTraslapes.seTraslapan(
                fecha,
                LocalTime.of(22, 0),
                LocalTime.of(6, 0),
                fecha.plusDays(1),
                LocalTime.of(5, 0),
                LocalTime.of(13, 0)
        ));
    }

    @Test
    void permitirTurnosSinTraslape() {
        assertFalse(ValidadorTraslapes.seTraslapan(
                fecha,
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                fecha.plusDays(1),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0)
        ));
    }

    @Test
    void detectarTraslapeConDiaAnterior() {
        assertTrue(ValidadorTraslapes.seTraslapan(
                fecha.minusDays(1),
                LocalTime.of(22, 0),
                LocalTime.of(6, 0),
                fecha,
                LocalTime.of(5, 30),
                LocalTime.of(13, 30)
        ));
    }
}
