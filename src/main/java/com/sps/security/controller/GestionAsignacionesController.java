
package com.sps.security.controller;

import com.sps.security.model.operative.Asignacion;
import com.sps.security.service.AsignacionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GestionAsignacionesController {

    @FXML
    private TextField campoIdGuardia;

    @FXML
    private TextField campoIdServicio;

    @FXML
    private TextField campoIdJornada;

    @FXML
    private DatePicker campoFechaAsignacion;

    @FXML
    private TableView<Asignacion> tablaAsignaciones;

    @FXML
    private TableColumn<Asignacion, String> columnaId;

    @FXML
    private TableColumn<Asignacion, String> columnaGuardia;

    @FXML
    private TableColumn<Asignacion, String> columnaServicio;

    @FXML
    private TableColumn<Asignacion, String> columnaJornada;

    @FXML
    private TableColumn<Asignacion, String> columnaFecha;

    @FXML
    private Button botonGuardar;

    private final AsignacionService asignacionService =
            new AsignacionService();

    private Asignacion asignacionSeleccionada;

    @FXML
    public void initialize() {

        columnaId.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getIdAsignacion()
                        )
                )
        );

        columnaGuardia.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getIdGuardia()
                        )
                )
        );

        columnaServicio.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getIdServicio()
                        )
                )
        );

        columnaJornada.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue().getIdJornada()
                        )
                )
        );

        columnaFecha.setCellValueFactory(data -> {
            LocalDate fecha =
                    data.getValue().getFechaAsignacion();

            return new SimpleStringProperty(
                    fecha == null ? "" : fecha.toString()
            );
        });

        tablaAsignaciones.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, actual) -> {
                    if (actual != null) {
                        seleccionarAsignacion(actual);
                    }
                });

        campoFechaAsignacion.setValue(LocalDate.now());

        cargarAsignaciones();
    }

    @FXML
    private void guardarAsignacion() {

        try {
            Integer idGuardia = convertirId(
                    campoIdGuardia.getText(),
                    "guardia"
            );

            Integer idServicio = convertirId(
                    campoIdServicio.getText(),
                    "servicio"
            );

            Integer idJornada = convertirId(
                    campoIdJornada.getText(),
                    "jornada"
            );

            LocalDate fecha =
                    campoFechaAsignacion.getValue();

            if (asignacionSeleccionada == null) {

                asignacionService.registrarAsignacion(
                        idGuardia,
                        idServicio,
                        idJornada,
                        fecha
                );

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Registro exitoso",
                        "La asignación se registró correctamente."
                );

            } else {

                asignacionService.actualizarAsignacion(
                        asignacionSeleccionada.getIdAsignacion(),
                        idGuardia,
                        idServicio,
                        idJornada,
                        fecha
                );

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Actualización exitosa",
                        "La asignación se actualizó correctamente."
                );
            }

            cargarAsignaciones();
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
    private void eliminarAsignacion() {

        if (asignacionSeleccionada == null) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "Selecciona una asignación de la tabla."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(
                "¿Deseas eliminar esta asignación?"
        );
        confirmacion.setContentText(
                "Esta acción eliminará el registro de la base de datos."
        );

        Optional<ButtonType> respuesta =
                confirmacion.showAndWait();

        if (respuesta.isEmpty()
                || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {

            asignacionService.eliminarAsignacion(
                    asignacionSeleccionada.getIdAsignacion()
            );

            cargarAsignaciones();
            limpiarFormulario();

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Asignación eliminada",
                    "La asignación se eliminó correctamente."
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al eliminar",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void nuevaAsignacion() {
        limpiarFormulario();
    }

    @FXML
    private void actualizarTabla() {
        limpiarFormulario();
        cargarAsignaciones();
    }

    private void seleccionarAsignacion(Asignacion asignacion) {

        asignacionSeleccionada = asignacion;

        campoIdGuardia.setText(
                String.valueOf(asignacion.getIdGuardia())
        );

        campoIdServicio.setText(
                String.valueOf(asignacion.getIdServicio())
        );

        campoIdJornada.setText(
                String.valueOf(asignacion.getIdJornada())
        );

        campoFechaAsignacion.setValue(
                asignacion.getFechaAsignacion()
        );

        botonGuardar.setText("Actualizar asignación");
    }

    private void cargarAsignaciones() {

        try {

            List<Asignacion> asignaciones =
                    asignacionService.listarAsignaciones();

            tablaAsignaciones.getItems().setAll(
                    asignaciones
            );

        } catch (RuntimeException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al consultar asignaciones",
                    e.getMessage()
            );
        }
    }

    private void limpiarFormulario() {

        tablaAsignaciones.getSelectionModel()
                .clearSelection();

        asignacionSeleccionada = null;

        campoIdGuardia.clear();
        campoIdServicio.clear();
        campoIdJornada.clear();

        campoFechaAsignacion.setValue(LocalDate.now());

        botonGuardar.setText("Registrar asignación");
    }

    private Integer convertirId(
            String texto,
            String nombreCampo) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "El ID de " + nombreCampo
                            + " es obligatorio."
            );
        }

        try {

            int id = Integer.parseInt(texto.trim());

            if (id <= 0) {
                throw new NumberFormatException();
            }

            return id;

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "El ID de " + nombreCampo
                            + " debe ser un número positivo."
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
