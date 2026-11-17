package com.sps.security.controller;

import com.sps.security.model.telemetry.Notificacion;
import com.sps.security.model.users.Usuario;
import com.sps.security.service.NotificacionService;
import com.sps.security.service.access_service.AuthService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class NotificacionesController {
    @FXML private TableView<Notificacion> tablaNotificaciones;
    @FXML private TableColumn<Notificacion, String> columnaFecha;
    @FXML private TableColumn<Notificacion, String> columnaMensaje;
    @FXML private TableColumn<Notificacion, String> columnaLeida;
    private final NotificacionService service = new NotificacionService();

    @FXML
    public void initialize() {
        columnaFecha.setCellValueFactory(c -> text(c.getValue().getFechaHora()));
        columnaMensaje.setCellValueFactory(c -> text(c.getValue().getMensaje()));
        columnaLeida.setCellValueFactory(c -> text(Boolean.TRUE.equals(
                c.getValue().getLeido()) ? "LEÍDA" : "NO LEÍDA"));
        cargar();
    }

    @FXML
    private void marcarLeida() {
        Notificacion seleccionada = tablaNotificaciones.getSelectionModel()
                .getSelectedItem();
        if (seleccionada == null) {
            mensaje("Selecciona una notificación.");
            return;
        }
        service.marcarComoLeida(seleccionada.getIdNotificacion());
        cargar();
    }

    @FXML
    private void marcarTodas() {
        Usuario usuario = AuthService.getCurrentUser();
        if (usuario != null) {
            service.marcarTodasComoLeidas(usuario.getIdUsuario());
            cargar();
        }
    }

    private void cargar() {
        Usuario usuario = AuthService.getCurrentUser();
        if (usuario != null) {
            tablaNotificaciones.getItems().setAll(
                    service.listarPorUsuario(usuario.getIdUsuario()));
        }
    }

    private SimpleStringProperty text(Object value) {
        return new SimpleStringProperty(value == null ? "" : String.valueOf(value));
    }

    private void mensaje(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, texto, ButtonType.OK);
        alerta.showAndWait();
    }
}
