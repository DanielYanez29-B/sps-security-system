package com.sps.security.util;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TableView;

public class TableUtils {

    /**
     * Ajusta automáticamente la altura de una TableView según sus filas visibles,
     * respetando un alto mínimo (cuando está vacía) y un alto máximo antes de activar scroll.
     */
    public static void configurarAlturaAdaptativa(TableView<?> tabla, double altoFila, double altoHeader, double altoMaximo) {
        tabla.setFixedCellSize(altoFila);

        var alturaCalculada = Bindings.createDoubleBinding(() -> {
            int filas = Math.max(tabla.getItems().size(), 1); // Mínimo espacio para 1 fila o mensaje "Sin datos"
            double total = altoHeader + (filas * altoFila) + 4; // +4px de margen de borde
            return Math.min(total, altoMaximo);
        }, tabla.getItems());

        tabla.prefHeightProperty().bind(alturaCalculada);
        tabla.minHeightProperty().bind(alturaCalculada);
        tabla.maxHeightProperty().bind(alturaCalculada);
    }
}