
package com.sps.security.controller;

import com.sps.security.model.operative.Jornada;
import com.sps.security.service.JornadaService;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class GestionJornadasController {

    @FXML private TableView<Jornada> tblJornadas;
    @FXML private TableColumn<Jornada, Integer> colId;
    @FXML private TableColumn<Jornada, String> colNombre;
    @FXML private TableColumn<Jornada, String> colInicio;
    @FXML private TableColumn<Jornada, String> colFin;
    @FXML private TableColumn<Jornada, String> colEstado;

    @FXML private TextField txtNombre;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;

    private final JornadaService jornadaService =
            new JornadaService();

    private final ObservableList<Jornada> listaJornadas =
            FXCollections.observableArrayList();

    private Jornada jornadaSeleccionada = null;

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {

        configurarTabla();
        cargarJornadas();

        tblJornadas.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, nueva) -> {

                    if (nueva != null) {
                        jornadaSeleccionada = nueva;
                        cargarFormulario(nueva);
                    }
                });
    }

    private void configurarTabla() {

        colId.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getIdJornada()
                ));

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNombre()
                ));

        colInicio.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formatearHora(
                                data.getValue().getHoraInicio()
                        )
                ));

        colFin.setCellValueFactory(data ->
                new SimpleStringProperty(
                        formatearHora(
                                data.getValue().getHoraFin()
                        )
                ));

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().isActivo()
                                ? "Activo"
                                : "Inactivo"
                ));

        tblJornadas.setItems(listaJornadas);
    }

    private void cargarJornadas() {

        try {
            listaJornadas.setAll(
                    jornadaService.listarJornadas()
            );
        } catch (RuntimeException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudieron cargar las jornadas: "
                            + e.getMessage()
            );
        }
    }

    @FXML
    private void handleGuardar() {

        try {
            String nombre = txtNombre.getText();

            LocalTime inicio = leerHora(
                    txtHoraInicio.getText()
            );

            LocalTime fin = leerHora(
                    txtHoraFin.getText()
            );

            jornadaService.registrarJornada(
                    nombre, inicio, fin
            );

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Jornada registrada correctamente."
            );

            cargarJornadas();
            handleLimpiar();

        } catch (RuntimeException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Error al registrar",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleActualizar() {

        if (jornadaSeleccionada == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Atención",
                    "Seleccione una jornada de la tabla."
            );
            return;
        }

        try {
            LocalTime inicio = leerHora(
                    txtHoraInicio.getText()
            );

            LocalTime fin = leerHora(
                    txtHoraFin.getText()
            );

            jornadaService.actualizarJornada(
                    jornadaSeleccionada.getIdJornada(),
                    txtNombre.getText(),
                    inicio,
                    fin
            );

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Jornada actualizada correctamente."
            );

            cargarJornadas();
            handleLimpiar();

        } catch (RuntimeException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Error al actualizar",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleDesactivar() {

        if (jornadaSeleccionada == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Atención",
                    "Seleccione una jornada."
            );
            return;
        }

        try {
            jornadaService.desactivarJornada(
                    jornadaSeleccionada.getIdJornada()
            );

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Jornada desactivada correctamente."
            );

            cargarJornadas();
            handleLimpiar();

        } catch (RuntimeException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Error",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleActivar() {

        if (jornadaSeleccionada == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Atención",
                    "Seleccione una jornada."
            );
            return;
        }

        try {
            jornadaService.activarJornada(
                    jornadaSeleccionada.getIdJornada()
            );

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Jornada activada correctamente."
            );

            cargarJornadas();
            handleLimpiar();

        } catch (RuntimeException e) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Error",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleLimpiar() {

        jornadaSeleccionada = null;

        txtNombre.clear();
        txtHoraInicio.clear();
        txtHoraFin.clear();

        tblJornadas.getSelectionModel()
                .clearSelection();
    }

    private void cargarFormulario(Jornada jornada) {

        txtNombre.setText(jornada.getNombre());

        txtHoraInicio.setText(
                formatearHora(jornada.getHoraInicio())
        );

        txtHoraFin.setText(
                formatearHora(jornada.getHoraFin())
        );
    }

    private LocalTime leerHora(String texto) {

        try {
            return LocalTime.parse(
                    texto.trim(),
                    FORMATO_HORA
            );

        } catch (DateTimeParseException |
                 NullPointerException e) {

            throw new IllegalArgumentException(
                    "Ingrese una hora válida en formato HH:mm. "
                            + "Ejemplo: 22:00"
            );
        }
    }

    private String formatearHora(LocalTime hora) {
        return hora == null
                ? ""
                : hora.format(FORMATO_HORA);
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
