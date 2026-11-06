package com.sps.security.controller;

import com.sps.security.model.users.Usuario;
import com.sps.security.service.access_service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {
    @FXML
    private Label lblGreeting;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Usuario u = AuthService.getCurrentUser();
        if (u != null){
            lblGreeting.setText("Bienvenido " + u.getUsername());
        }
    }
}
