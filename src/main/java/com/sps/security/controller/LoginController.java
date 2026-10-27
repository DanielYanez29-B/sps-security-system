package com.sps.security.controller;

import com.sps.security.service.access_service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    private final AuthService loginController = new AuthService();
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label advertenciaLabel;


    @FXML
    public void autenticar(ActionEvent event) {
        String email = txtEmail.getText();
        String contrasena = txtPassword.getText();

        if (loginController.verificarUsuario(email, contrasena).isEmpty()) {
            advertenciaLabel.setText("Emai o contraseña incorrecto");
        }
    }
}
