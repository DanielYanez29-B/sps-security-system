package com.sps.security;

import com.sps.security.util.TareasBackground;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PruebaVisualServicios extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/com/sps/security/gestion-servicios.fxml"
                )
        );

        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 700);

        stage.setTitle("S.P.S. - Prueba de Gestión de Servicios");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        TareasBackground.detenerTodo();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
