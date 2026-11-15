
package com.sps.security.controller;

import com.sps.security.model.operative.Guardia;
import com.sps.security.service.GuardiaService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.util.List;

public class GestionGuardiasController {

    @FXML
    private TableView<Guardia> tablaGuardias;

    @FXML
    private TableColumn<Guardia, String> columnaId;

    @FXML
    private TableColumn<Guardia, String> columnaNombre;

    @FXML
    private TableColumn<Guardia, String> columnaRfc;

    @FXML
    private TableColumn<Guardia, String> columnaFecha;

    @FXML
    private TableColumn<Guardia, String> columnaEstado;

    @FXML
    private TextField campoIdUsuario;

    @FXML
    private TextField campoNombres;

    @FXML
    private TextField campoApellidoPaterno;

    @FXML
    private TextField campoApellidoMaterno;

    @FXML
    private TextField campoRfc;

    @FXML
    private DatePicker campoFechaIngreso;

    @FXML
    private Button botonGuardar;

    private final GuardiaService guardiaService =
            new GuardiaService();

    private Guardia guardiaSeleccionado;

    @FXML
    public void initialize() {

        columnaId.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getIdGuardia()
                        )
                )
        );

        columnaNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().toString()
                )
        );

        columnaRfc.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getRfc() == null
                                ? "Sin RFC"
                                : data.getValue().getRfc()
                )
        );

        columnaFecha.setCellValueFactory(data -> {
            LocalDate fecha =
                    data.getValue().getFechaIngreso();

            return new SimpleStringProperty(
                    fecha == null ? "" : fecha.toString()
            );
        });

        columnaEstado.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().isEstatusActivo()
                                ? "Activo"
                                : "Inactivo"
                )
        );

        tablaGuardias.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, actual) -> {
                    if (actual != null) {
                        seleccionarGuardia(actual);
                    }
                });

        cargarGuardias();
    }

    @FXML
    private void guardarGuardia() {

        try {
            Integer idUsuario =
                    convertirIdUsuario(campoIdUsuario.getText());

            String nombres = campoNombres.getText();
            String apellidoPaterno =
                    campoApellidoPaterno.getText();
            String apellidoMaterno =
                    campoApellidoMaterno.getText();
            String rfc = campoRfc.getText();
            LocalDate fecha = campoFechaIngreso.getValue();

            if (guardiaSeleccionado == null) {

                guardiaService.registrarGuardia(
                        idUsuario,
                        nombres,
                        apellidoPaterno,
                        apellidoMaterno,
                        rfc,
                        fecha
                );

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Registro exitoso",
                        "El guardia se registró correctamente."
                );

            } else {

                guardiaService.actualizarGuardia(
                        guardiaSeleccionado.getIdGuardia(),
                        idUsuario,
                        nombres,
                        apellidoPaterno,
                        apellidoMaterno,
                        rfc,
                        fecha
                );

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Actualización exitosa",
                        "El guardia se actualizó correctamente."
                );
            }

            cargarGuardias();
            limpiarFormulario();

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo guardar",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void activarGuardia() {

        if (guardiaSeleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "Selecciona un guardia de la tabla."
            );
            return;
        }

        try {

            guardiaService.activarGuardia(
                    guardiaSeleccionado.getIdGuardia()
            );

            cargarGuardias();
            limpiarFormulario();

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Guardia activado",
                    "El guardia se activó correctamente."
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void desactivarGuardia() {

        if (guardiaSeleccionado == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "Selecciona un guardia de la tabla."
            );
            return;
        }

        try {

            guardiaService.desactivarGuardia(
                    guardiaSeleccionado.getIdGuardia()
            );

            cargarGuardias();
            limpiarFormulario();

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Guardia desactivado",
                    "El guardia se desactivó correctamente."
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void nuevoGuardia() {
        limpiarFormulario();
    }

    @FXML
    private void actualizarTabla() {
        cargarGuardias();
        limpiarFormulario();
    }

    private void seleccionarGuardia(Guardia guardia) {

        guardiaSeleccionado = guardia;

        campoIdUsuario.setText(
                guardia.getIdUsuario() == null
                        ? ""
                        : String.valueOf(guardia.getIdUsuario())
        );

        campoNombres.setText(guardia.getNombres());

        campoApellidoPaterno.setText(
                guardia.getApellidoPaterno()
        );

        campoApellidoMaterno.setText(
                guardia.getApellidoMaterno() == null
                        ? ""
                        : guardia.getApellidoMaterno()
        );

        campoRfc.setText(
                guardia.getRfc() == null
                        ? ""
                        : guardia.getRfc()
        );

        campoFechaIngreso.setValue(
                guardia.getFechaIngreso()
        );

        botonGuardar.setText("Actualizar guardia");
    }

    private void cargarGuardias() {

        try {

            List<Guardia> guardias =
                    guardiaService.listarGuardias();

            tablaGuardias.getItems().setAll(guardias);

        } catch (RuntimeException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al consultar guardias",
                    e.getMessage()
            );
        }
    }

    private void limpiarFormulario() {

        tablaGuardias.getSelectionModel().clearSelection();

        guardiaSeleccionado = null;

        campoIdUsuario.clear();
        campoNombres.clear();
        campoApellidoPaterno.clear();
        campoApellidoMaterno.clear();
        campoRfc.clear();
        campoFechaIngreso.setValue(null);

        botonGuardar.setText("Registrar guardia");
    }

    private Integer convertirIdUsuario(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        try {

            int id = Integer.parseInt(texto.trim());

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "El ID de usuario debe ser un número positivo."
            );
        }
    }

    private void mostrarMensaje(
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
