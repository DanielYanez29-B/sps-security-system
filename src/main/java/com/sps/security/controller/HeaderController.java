package com.sps.security.controller;

import com.sps.security.model.users.Usuario;
import com.sps.security.service.access_service.AuthService;
import com.sps.security.util.TareasBackground;
import com.sps.security.service.NotificacionService;
import com.sps.security.util.NavigationService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {
    @FXML
    private Label lblGreeting;
    @FXML
    private Label lblStatusTime;
    @FXML
    private Label lblNotificationCount;

    private final NotificacionService notificacionService = new NotificacionService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Usuario u = AuthService.getCurrentUser();
        if (u != null){
            lblGreeting.setText("Bienvenido " + u.getUsername());
            actualizarContador(u.getIdUsuario());
        }


        Timeline reloj = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> lblStatusTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))));
        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();
        TareasBackground.registrarCierre(reloj::stop);
    }

    @FXML
    private void onNotifications() {
        NavigationService.getInstance()
                .cargarVistaEnContenido("notificaciones-view.fxml");
    }

    private void actualizarContador(Integer idUsuario) {
        try {
            lblNotificationCount.setText(String.valueOf(
                    notificacionService.listarNoLeidas(idUsuario).size()));
        } catch (RuntimeException exception) {
            lblNotificationCount.setText("0");
            System.err.println("No se pudo cargar el contador de notificaciones: "
                    + exception.getMessage());
        }
    }



}
