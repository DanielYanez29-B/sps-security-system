package com.sps.security.controller;

import com.sps.security.model.users.Usuario;
import com.sps.security.service.access_service.AuthService;
import com.sps.security.util.TareasBackground;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Usuario u = AuthService.getCurrentUser();
        if (u != null){
            lblGreeting.setText("Bienvenido " + u.getUsername());
        }


        Timeline reloj = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> lblStatusTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))));
        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();
        TareasBackground.registrarCierre(reloj::stop);
    }



}
