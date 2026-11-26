package com.sps.security.controller;

import com.sps.security.model.enums.EstadoAlarma;
import com.sps.security.service.AlarmaService;
import com.sps.security.util.AlarmaEventBus;
import com.sps.security.util.NavigationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.awt.Toolkit;
import java.util.ArrayDeque;
import java.util.Queue;

public class MainController {
    private final AlarmaService alarmaService = new AlarmaService();
    private final Queue<com.sps.security.model.telemetry.Alarma> colaAlarmas =
            new ArrayDeque<>();
    private boolean mostrandoAlarma;

    @FXML
    public void initialize() {

        AlarmaEventBus.suscribir(this::mostrarAlarma);
        Platform.runLater(() ->
                NavigationService.getInstance().cargarVistaEnContenido("dashboard-view.fxml"));
    }

    private void mostrarAlarma(com.sps.security.model.telemetry.Alarma alarma) {
        Platform.runLater(() -> {
            colaAlarmas.add(alarma);
            mostrarSiguienteAlarma();
        });
    }

    private void mostrarSiguienteAlarma() {
        if (mostrandoAlarma || colaAlarmas.isEmpty()) {
            return;
        }
        mostrandoAlarma = true;
        com.sps.security.model.telemetry.Alarma alarma = colaAlarmas.remove();
        Toolkit.getDefaultToolkit().beep();
        try {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Alarma detectada");
            alerta.setHeaderText(alarma.getNivelPrioridad() + " - "
                    + alarma.getTipoAlarma());
            alerta.setContentText("Dispositivo: " + alarma.getIdDispositivo()
                    + "\nHora: " + alarma.getFechaHora()
                    + "\n" + (alarma.getDescripcion() == null
                    ? "" : alarma.getDescripcion()));
            ButtonType atender = new ButtonType("Confirmar y atender");
            ButtonType falsa = new ButtonType("Falsa alarma");
            alerta.getButtonTypes().setAll(atender, falsa, ButtonType.CANCEL);
            alerta.showAndWait().ifPresent(respuesta -> {
                if (respuesta == atender) {
                    alarmaService.cambiarEstado(alarma.getIdAlarma(),
                            EstadoAlarma.ATENDIDA);
                } else if (respuesta == falsa) {
                    alarmaService.cambiarEstado(alarma.getIdAlarma(),
                            EstadoAlarma.FALSA_ALARMA);
                }
            });
        } finally {
            mostrandoAlarma = false;
            mostrarSiguienteAlarma();
        }
    }
}
