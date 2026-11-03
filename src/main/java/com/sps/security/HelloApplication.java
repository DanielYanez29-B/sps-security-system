package com.sps.security;

import com.sps.security.util.NavigationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        NavigationService.getInstance().setPrimaryStage(stage);

        stage.setTitle("Sistema de Monitoreo SPS");
        NavigationService.getInstance().cargarVista("login-view.fxml");;

    }
}
