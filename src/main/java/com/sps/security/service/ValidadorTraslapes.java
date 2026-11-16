
package com.sps.security.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ValidadorTraslapes {

    public static boolean seTraslapan(
            LocalDate fecha1,
            LocalTime inicio1,
            LocalTime fin1,
            LocalDate fecha2,
            LocalTime inicio2,
            LocalTime fin2) {

        LocalDateTime inicioA = LocalDateTime.of(fecha1, inicio1);
        LocalDateTime finA = LocalDateTime.of(fecha1, fin1);

        LocalDateTime inicioB = LocalDateTime.of(fecha2, inicio2);
        LocalDateTime finB = LocalDateTime.of(fecha2, fin2);

        if (!finA.isAfter(inicioA)) {
            finA = finA.plusDays(1);
        }

        if (!finB.isAfter(inicioB)) {
            finB = finB.plusDays(1);
        }

        return inicioA.isBefore(finB)
                && inicioB.isBefore(finA);
    }
}
