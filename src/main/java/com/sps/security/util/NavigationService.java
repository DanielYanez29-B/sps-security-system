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
    private boolean darkTheme = false; // Mantiene el estado del tema entre vistas

    private static final String FXML_PATH = "/com/sps/security/";
    private static final String CSS_PATH = "/com/sps/security/sps-theme.css";

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

    // --- MÉTODOS PARA EL TEMA ---

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void toggleTheme() {
        darkTheme = !darkTheme;
        if (stage != null && stage.getScene() != null) {
            aplicarClaseTema(stage.getScene().getRoot());
        }
    }

    private void aplicarClaseTema(Parent root) {
        if (darkTheme) {
            if (!root.getStyleClass().contains("dark-theme")) {
                root.getStyleClass().add("dark-theme");
            }
        } else {
            root.getStyleClass().remove("dark-theme");
        }
    }

    private void aplicarEscena(Parent root) {
        aplicarClaseTema(root);

        if (stage.getScene() == null) {
            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource(CSS_PATH);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("No se encontró el archivo CSS: " + CSS_PATH);
            }
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }

        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    // --- MÉTODOS DE NAVEGACIÓN ACTUALIZADOS ---

    public void cargarVista(String fxmlName) {
        try {
            Parent root = cargarFXML(fxmlName);
            aplicarEscena(root);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista FXML: " + fxmlName);
            e.printStackTrace();
        }
    }

    public <T> T cargarVistaConControlador(String fxmlName) {
        try {
            URL url = obtenerURL(fxmlName);
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            aplicarEscena(root);
            return loader.getController();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + fxmlName);
            e.printStackTrace();
            return null;
        }
    }

    public void cargarVistaEnContenido(String fxmlName) {
        if (stage == null || stage.getScene() == null) {
            throw new IllegalStateException("La ventana principal no está inicializada.");
        }

        Node nodo = stage.getScene().getRoot().lookup("#contentArea");

        if (!(nodo instanceof StackPane contentArea)) {
            throw new IllegalStateException("No se encontró el StackPane contentArea en la pantalla principal.");
        }

        try {
            Parent vista = cargarFXML(fxmlName);
            contentArea.getChildren().setAll(vista);
        } catch (IOException e) {
            System.err.println("Error al cargar el contenido: " + fxmlName);
            e.printStackTrace();
        }
    }

    private Parent cargarFXML(String fxmlName) throws IOException {
        FXMLLoader loader = new FXMLLoader(obtenerURL(fxmlName));
        return loader.load();
    }

    private URL obtenerURL(String fxmlName) throws IOException {
        String ruta = FXML_PATH + fxmlName;
        URL url = getClass().getResource(ruta);
        if (url == null) {
            throw new IOException("No se encontró el archivo FXML: " + ruta);
        }
        return url;
    }
}