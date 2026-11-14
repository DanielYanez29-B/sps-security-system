package com.sps.security.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class NavigationService {

    private static NavigationService instance;

    private Stage stage;

    private static final String FXML_PATH =
            "/com/sps/security/";

    private NavigationService() {
    }

    public static NavigationService getInstance() {
        if (instance == null) {
            instance = new NavigationService();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    public void cargarVista(String fxmlName) {
        try {
            Parent root = cargarFXML(fxmlName);

            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                stage.getScene().setRoot(root);
            }

            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println(
                    "Error al cargar la vista FXML: " + fxmlName
            );
            e.printStackTrace();
        }
    }

    public <T> T cargarVistaConControlador(String fxmlName) {
        try {
            URL url = obtenerURL(fxmlName);

            FXMLLoader loader = new FXMLLoader(url);
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
            System.err.println(
                    "Error al cargar la vista: " + fxmlName
            );
            e.printStackTrace();
            return null;
        }
    }

    public void cargarVistaEnContenido(String fxmlName) {
        if (stage == null || stage.getScene() == null) {
            throw new IllegalStateException(
                    "La ventana principal no está inicializada."
            );
        }

        Node nodo = stage.getScene()
                .getRoot()
                .lookup("#contentArea");

        if (!(nodo instanceof StackPane contentArea)) {
            throw new IllegalStateException(
                    "No se encontró el StackPane contentArea "
                            + "en la pantalla principal."
            );
        }

        try {
            Parent vista = cargarFXML(fxmlName);

            contentArea.getChildren().setAll(vista);

        } catch (IOException e) {
            System.err.println(
                    "Error al cargar el contenido: " + fxmlName
            );
            e.printStackTrace();
        }
    }

    private Parent cargarFXML(String fxmlName)
            throws IOException {

        FXMLLoader loader = new FXMLLoader(
                obtenerURL(fxmlName)
        );

        return loader.load();
    }

    private URL obtenerURL(String fxmlName)
            throws IOException {

        String ruta = FXML_PATH + fxmlName;

        URL url = getClass().getResource(ruta);

        if (url == null) {
            throw new IOException(
                    "No se encontró el archivo FXML: " + ruta
            );
        }

        return url;
    }
}
