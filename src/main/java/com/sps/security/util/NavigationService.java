package com.sps.security.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class NavigationService {

    private static NavigationService instance;
    private Stage stage;

    // Ruta base donde se encuentran tus vistas FXML
    private static final String FXML_PATH = "/com/sps/security/";

    private NavigationService() {
        // Constructor privado para patrón Singleton
    }

    public static NavigationService getInstance() {
        if (instance == null) {
            instance = new NavigationService();
        }
        return instance;
    }

    /**
     * Debe llamarse una sola vez al iniciar la aplicación en el método start() de Main.
     */
    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Carga y cambia la vista principal en la ventana actual.
     * @param fxmlName Nombre del archivo FXML (ejemplo: "Login.fxml" o "Dashboard.fxml")
     */
    public void cargarVista(String fxmlName) {
        try {
            String fullPath = FXML_PATH + fxmlName;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fullPath));
            Parent root = loader.load();

            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                stage.getScene().setRoot(root);
            }

            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la vista FXML: " + fxmlName);
            e.printStackTrace();
        }
    }

    /**
     * Sobrecarga del método cargarVista para devolver el controlador si se necesita pasar parámetros.
     */
    public <T> T cargarVistaConControlador(String fxmlName) {
        try {
            String fullPath = FXML_PATH + fxmlName;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fullPath));
            Parent root = loader.load();

            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                stage.getScene().setRoot(root);
            }

            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

            return loader.getController();

        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + fxmlName);
            e.printStackTrace();
            return null;
        }
    }
}