package com.sps.security.controller;

import com.sps.security.model.enums.NivelPrioridad;
import com.sps.security.model.telemetry.Alarma;
import com.sps.security.model.users.Usuario;
import com.sps.security.service.DashboardService;
import com.sps.security.service.DashboardService.Resumen;
import com.sps.security.service.access_service.AuthService;
import com.sps.security.util.AlarmaEventBus;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class DashboardController {

    @FXML private VBox root;
    @FXML private Label lblPendientes, lblGuardias, lblDispositivos, lblNoLeidas;
    @FXML private Label lblActualizado;
    @FXML private VBox contenedorPrioridades;

    @FXML private TableView<Alarma> tablaUltimas;
    @FXML private TableColumn<Alarma, String> colFecha, colTipo, colPrioridad, colEstado;

    private final DashboardService service = new DashboardService();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
    private Timeline refresco;
    private Consumer<Alarma> listenerAlarma;

    @FXML
    public void initialize() {
        configurarTabla();

        refresco = new Timeline(new KeyFrame(Duration.seconds(5), e -> refrescar()));
        refresco.setCycleCount(Timeline.INDEFINITE);
        refresco.play();

        // refresco inmediato cuando llega una alarma nueva
        listenerAlarma = a -> Platform.runLater(this::refrescar);
        AlarmaEventBus.suscribir(listenerAlarma);

        // al cambiar de pantalla el nodo sale de la escena: limpiamos recursos
        root.sceneProperty().addListener((obs, vieja, nueva) -> {
            if (nueva == null) detener();
        });

        refrescar();
    }

    private void detener() {
        if (refresco != null) refresco.stop();
        if (listenerAlarma != null) AlarmaEventBus.cancelar(listenerAlarma);
    }

    private void refrescar() {
        Usuario u = AuthService.getCurrentUser();
        Integer idUsuario = u != null ? u.getIdUsuario() : null;

        // consulta a BD fuera del hilo de JavaFX
        Thread t = new Thread(() -> {
            try {
                Resumen r = service.obtenerResumen(idUsuario);
                Platform.runLater(() -> pintar(r));
            } catch (RuntimeException ex) {
                System.err.println("Dashboard: " + ex.getMessage());
            }
        }, "dashboard-refresh");
        t.setDaemon(true);
        t.start();
    }

    private void pintar(Resumen r) {
        lblPendientes.setText(String.valueOf(r.alarmasPendientes()));
        lblGuardias.setText(String.valueOf(r.guardiasEnTurno()));
        lblDispositivos.setText(String.valueOf(r.dispositivosActivos()));
        lblNoLeidas.setText(String.valueOf(r.notificacionesNoLeidas()));
        tablaUltimas.getItems().setAll(r.ultimasAlarmas());
        pintarPrioridades(r);
        lblActualizado.setText("Actualizado " +
                java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private void pintarPrioridades(Resumen r) {
        contenedorPrioridades.getChildren().clear();
        long max = Math.max(1, r.pendientesPorPrioridad().values().stream()
                .mapToLong(Long::longValue).max().orElse(1));

        for (NivelPrioridad p : NivelPrioridad.values()) {
            long cantidad = r.pendientesPorPrioridad().getOrDefault(p, 0L);

            Label nombre = new Label(p.name());
            nombre.getStyleClass().add("dash-prio-nombre");
            nombre.setMinWidth(70);

            ProgressBar barra = new ProgressBar((double) cantidad / max);
            barra.setMaxWidth(Double.MAX_VALUE);
            barra.getStyleClass().addAll("dash-bar", "dash-bar-" + p.name().toLowerCase());
            HBox.setHgrow(barra, Priority.ALWAYS);

            Label valor = new Label(String.valueOf(cantidad));
            valor.getStyleClass().add("dash-prio-valor");
            valor.setMinWidth(30);

            HBox fila = new HBox(12, nombre, barra, valor);
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            contenedorPrioridades.getChildren().add(fila);
        }
    }

    private void configurarTabla() {
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaHora().format(fmt)));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipoAlarma()));
        colPrioridad.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getNivelPrioridad())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getEstado())));

        colPrioridad.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("prio-critica", "prio-alta", "prio-media", "prio-baja");
                if (empty || item == null) { setText(null); return; }
                setText(item);
                getStyleClass().add("prio-" + item.toLowerCase());
            }
        });

        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("estado-pendiente", "estado-atendida", "estado-falsa_alarma");
                if (empty || item == null) { setText(null); return; }
                setText(item.replace('_', ' '));
                getStyleClass().add("estado-" + item.toLowerCase());
            }
        });

        tablaUltimas.setPlaceholder(new Label("Sin alarmas registradas"));
    }
}