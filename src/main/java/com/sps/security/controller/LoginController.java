package com.sps.security.controller;

import com.sps.security.service.access_service.AuthService;
import com.sps.security.util.NavigationService;
import javafx.application.Platform;
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
    private Label lblError;;


    @FXML
    public void autenticar(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String contrasena = txtPassword.getText();

        // 1. Validar que no haya campos vacíos
        if (email.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor, llena todos los campos");
            return;
        }

        // 2. Guardar el resultado en una variable para evitar consultar 2 veces a la BD
        var usuarioOpt = loginController.verificarUsuario(email, contrasena);

        if (usuarioOpt.isEmpty()) {
            mostrarError("Email o contraseña incorrectos");
        } else {
            ocultarError(); // Limpiar aviso de error
            NavigationService.getInstance().cargarVista("MainLayout.fxml");;
        }
    }
    private void mostrarError(String mensaje) {
        // Platform.runLater asegura la actualización si la consulta corre en un hilo secundario/DB
        Platform.runLater(() -> {
            lblError.setText(mensaje);
            lblError.setVisible(true);
            lblError.setManaged(true);
        });
    }

    /**
     * Oculta la advertencia y libera el espacio en el VBox.
     */
    private void ocultarError() {
        Platform.runLater(() -> {
            lblError.setText("");
            lblError.setVisible(false);
            lblError.setManaged(false);
        });
    }
}
