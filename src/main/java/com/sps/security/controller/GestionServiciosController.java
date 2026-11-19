package com.sps.security.controller;

import com.sps.security.model.operative.Servicio;
import com.sps.security.service.ServicioService;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class GestionServiciosController {

    private final ServicioService servicioService = new ServicioService();

    private Servicio servicioSeleccionado;

    // Tabla de servicios
    @FXML private TableView<Servicio> tblServicios;
    @FXML private TableColumn<Servicio, Integer> colId;
    @FXML private TableColumn<Servicio, Integer> colCliente;
    @FXML private TableColumn<Servicio, String> colNombre;
    @FXML private TableColumn<Servicio, String> colDireccion;
    @FXML private TableColumn<Servicio, LocalDate> colFechaInicio;
    @FXML private TableColumn<Servicio, String> colEstado;

    // Campos del formulario
    @FXML private TextField txtIdCliente;
    @FXML private TextField txtNombreServicio;
    @FXML private TextField txtDireccion;
    @FXML private DatePicker dpFechaInicio;
    @FXML private ComboBox<String> cmbEstado;

    @FXML
    public void initialize() {
        configurarTabla();

        cmbEstado.setItems(
                FXCollections.observableArrayList(
                        "ACTIVO", "INACTIVO"
                )
        );
        cmbEstado.setValue("ACTIVO");

        tblServicios.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        cargarFormulario(seleccionado);
                    } else {
                        servicioSeleccionado = null;
                    }
                });

        cargarServicios();
    }

    private void configurarTabla() {

        colId.setCellValueFactory(
                dato -> new ReadOnlyObjectWrapper<>(
                        dato.getValue().getIdServicio()
                )
        );

        colCliente.setCellValueFactory(
                dato -> new ReadOnlyObjectWrapper<>(
                        dato.getValue().getIdCliente()
                )
        );

        colNombre.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getNombreServicio()
                )
        );

        colDireccion.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getDireccion()
                )
        );

        colFechaInicio.setCellValueFactory(
                dato -> new ReadOnlyObjectWrapper<>(
                        dato.getValue().getFechaInicio()
                )
        );

        colEstado.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getEstado()
                )
        );
    }

    private void cargarServicios() {
        try {
            tblServicios.setItems(
                    FXCollections.observableArrayList(
                            servicioService.listarServicios()
                    )
            );
        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar servicios",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleGuardar() {
        try {
            Servicio servicio = construirServicio(null);

            servicioService.registrarServicio(servicio);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Servicio registrado correctamente."
            );

            handleLimpiar();
            cargarServicios();

        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al guardar",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleActualizar() {

        if (servicioSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Aviso",
                    "Selecciona un servicio de la tabla."
            );
            return;
        }

        try {
            Servicio servicio = construirServicio(
                    servicioSeleccionado.getIdServicio()
            );

            servicioService.actualizarServicio(servicio);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Servicio actualizado correctamente."
            );

            handleLimpiar();
            cargarServicios();

        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al actualizar",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleEliminar() {

        if (servicioSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Aviso",
                    "Selecciona un servicio de la tabla."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Estás seguro de eliminar el servicio "
                        + servicioSeleccionado.getNombreServicio() + "?"
        );

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        try {
            servicioService.eliminarServicio(servicioSeleccionado);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Servicio eliminado correctamente."
            );

            handleLimpiar();
            cargarServicios();

        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al eliminar",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleLimpiar() {
        txtIdCliente.clear();
        txtNombreServicio.clear();
        txtDireccion.clear();
        dpFechaInicio.setValue(null);
        cmbEstado.setValue("ACTIVO");

        servicioSeleccionado = null;
        tblServicios.getSelectionModel().clearSelection();
    }

    private Servicio construirServicio(Integer id) {

        String textoIdCliente = txtIdCliente.getText().trim();

        if (textoIdCliente.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe indicar el ID del cliente."
            );
        }

        Integer idCliente;

        try {
            idCliente = Integer.valueOf(textoIdCliente);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El ID del cliente debe ser un número entero válido."
            );
        }

        return new Servicio(
                id,
                idCliente,
                obtenerTexto(txtNombreServicio),
                obtenerTexto(txtDireccion),
                dpFechaInicio.getValue(),
                cmbEstado.getValue()
        );
    }

    private String obtenerTexto(TextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }

    private void cargarFormulario(Servicio servicio) {
        servicioSeleccionado = servicio;

        txtIdCliente.setText(
                String.valueOf(servicio.getIdCliente())
        );
        txtNombreServicio.setText(servicio.getNombreServicio());
        txtDireccion.setText(servicio.getDireccion());
        dpFechaInicio.setValue(servicio.getFechaInicio());
        cmbEstado.setValue(servicio.getEstado());
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(
                mensaje == null ? "Ha ocurrido un error." : mensaje
        );
        alerta.showAndWait();
    }
}