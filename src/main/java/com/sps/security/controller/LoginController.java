package com.sps.security.controller;

import com.sps.security.service.access_service.AuthService;
import com.sps.security.util.NavigationService;
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
        String email = txtEmail.getText().trim();
        String contrasena = txtPassword.getText();

        // 1. Validar que no haya campos vacíos
        if (email.isEmpty() || contrasena.isEmpty()) {
            advertenciaLabel.setText("Por favor, llena todos los campos");
            return;
        }

        // 2. Guardar el resultado en una variable para evitar consultar 2 veces a la BD
        var usuarioOpt = loginController.verificarUsuario(email, contrasena);

        if (usuarioOpt.isEmpty()) {
            advertenciaLabel.setText("Email o contraseña incorrectos");
        } else {
            advertenciaLabel.setText(""); // Limpiar aviso de error
            AuthService.setCurrentUser(usuarioOpt.get());
            NavigationService.getInstance().cargarVista("usuarios-view.fxml");;

        }
    }
}
