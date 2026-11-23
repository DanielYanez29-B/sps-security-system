package com.sps.security.controller;

import com.sps.security.model.operative.Servicio;
import com.sps.security.service.ServicioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GestionServiciosController {

    @FXML
    private TextField campoIdCliente;

    @FXML
    private TextField campoNombreServicio;

    @FXML
    private TextField campoDireccion;

    @FXML
    private DatePicker campoFechaInicio;

    @FXML
    private ComboBox<String> comboEstado;

    @FXML
    private Button botonGuardar;

    @FXML
    private CheckBox checkSoloActivos;

    @FXML
    private TableView<Servicio> tablaServicios;

    @FXML
    private TableColumn<Servicio, Integer> columnaId;

    @FXML
    private TableColumn<Servicio, Integer> columnaIdCliente;

    @FXML
    private TableColumn<Servicio, String> columnaNombre;

    @FXML
    private TableColumn<Servicio, String> columnaDireccion;

    @FXML
    private TableColumn<Servicio, LocalDate> columnaFechaInicio;

    @FXML
    private TableColumn<Servicio, String> columnaEstado;
    @FXML private StackPane modalFormulario;
    @FXML private Label lblModalTitulo;

    private final ServicioService servicioService;
    private final ObservableList<Servicio> listaServicios;
    private Servicio servicioSeleccionado;

    public GestionServiciosController() {
        this.servicioService = new ServicioService();
        this.listaServicios = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        comboEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        comboEstado.setValue("ACTIVO");

        configurarColumnas();
        tablaServicios.setItems(listaServicios);

        tablaServicios.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, actual) -> cargarServicioEnFormulario(actual)
        );
        tablaServicios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tablaServicios.getSelectionModel().getSelectedItem() != null) {
                lblModalTitulo.setText("Editar Servicio");
                mostrarFormulario();
            }
        });

        actualizarTabla();
    }

    @FXML
    public void abrirFormularioNuevo() {
        limpiarFormulario();
        lblModalTitulo.setText("Nuevo Servicio");
        mostrarFormulario();
    }

    @FXML
    public void mostrarFormulario() {
        modalFormulario.setVisible(true);
        modalFormulario.setManaged(true);
    }

    @FXML
    public void ocultarFormulario() {
        modalFormulario.setVisible(false);
        modalFormulario.setManaged(false);
    }

    private void configurarColumnas() {
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idServicio"));
        columnaIdCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombreServicio"));
        columnaDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        columnaFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    @FXML
    private void guardarServicio() {
        try {
            Integer idCliente = parsearIdCliente();
            String nombreServicio = campoNombreServicio.getText();
            String direccion = campoDireccion.getText();
            LocalDate fechaInicio = campoFechaInicio.getValue();
            String estado = comboEstado.getValue();

            if (servicioSeleccionado == null) {
                Servicio nuevoServicio = new Servicio();
                nuevoServicio.setIdCliente(idCliente);
                nuevoServicio.setNombreServicio(nombreServicio);
                nuevoServicio.setDireccion(direccion);
                nuevoServicio.setFechaInicio(fechaInicio);
                nuevoServicio.setEstado(estado);

                servicioService.registrarServicio(nuevoServicio);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso", "El servicio se registró correctamente.");
            } else {
                servicioSeleccionado.setIdCliente(idCliente);
                servicioSeleccionado.setNombreServicio(nombreServicio);
                servicioSeleccionado.setDireccion(direccion);
                servicioSeleccionado.setFechaInicio(fechaInicio);
                servicioSeleccionado.setEstado(estado);

                servicioService.actualizarServicio(servicioSeleccionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización exitosa", "El servicio se actualizó correctamente.");
            }

            limpiarFormulario();
            actualizarTabla();
            ocultarFormulario();

        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos inválidos", e.getMessage());
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de operación", e.getMessage());
        }
    }

    @FXML
    private void nuevoServicio() {
        limpiarFormulario();
    }

    @FXML
    private void activarServicio() {
        cambiarEstadoServicio("ACTIVO");
    }

    @FXML
    private void desactivarServicio() {
        cambiarEstadoServicio("INACTIVO");
    }

    @FXML
    private void eliminarServicio() {
        if (servicioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Selecciona un servicio de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas eliminar el servicio \"" + servicioSeleccionado.getNombreServicio() + "\"?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                servicioService.eliminarServicio(servicioSeleccionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Servicio eliminado", "El servicio fue eliminado correctamente.");
                limpiarFormulario();
                actualizarTabla();
                ocultarFormulario();
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", e.getMessage());
            } catch (RuntimeException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al eliminar", e.getMessage());
            }
        }
    }

    @FXML
    private void actualizarTabla() {
        try {
            List<Servicio> servicios = checkSoloActivos.isSelected()
                    ? servicioService.listarServiciosActivos()
                    : servicioService.listarServicios();

            listaServicios.setAll(servicios);
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar datos", "No se pudieron obtener los servicios: " + e.getMessage());
        }
    }

    private void cambiarEstadoServicio(String nuevoEstado) {
        if (servicioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Selecciona un servicio de la tabla.");
            return;
        }

        try {
            servicioSeleccionado.setEstado(nuevoEstado);
            servicioService.actualizarServicio(servicioSeleccionado);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Estado actualizado", "El servicio ahora está en estado " + nuevoEstado + ".");
            limpiarFormulario();
            actualizarTabla();
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", e.getMessage());
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar estado", e.getMessage());
        }
    }

    private void cargarServicioEnFormulario(Servicio servicio) {
        this.servicioSeleccionado = servicio;

        if (servicio == null) {
            botonGuardar.setText("Registrar servicio");
            return;
        }

        campoIdCliente.setText(servicio.getIdCliente() != null ? String.valueOf(servicio.getIdCliente()) : "");
        campoNombreServicio.setText(servicio.getNombreServicio());
        campoDireccion.setText(servicio.getDireccion());
        campoFechaInicio.setValue(servicio.getFechaInicio());
        comboEstado.setValue(servicio.getEstado() != null ? servicio.getEstado() : "ACTIVO");
        botonGuardar.setText("Actualizar servicio");
    }

    private void limpiarFormulario() {
        servicioSeleccionado = null;
        campoIdCliente.clear();
        campoNombreServicio.clear();
        campoDireccion.clear();
        campoFechaInicio.setValue(null);
        comboEstado.setValue("ACTIVO");
        botonGuardar.setText("Registrar servicio");
        tablaServicios.getSelectionModel().clearSelection();
    }

    private Integer parsearIdCliente() {
        String textoId = campoIdCliente.getText();
        if (textoId == null || textoId.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar o ingresar un ID de cliente.");
        }
        try {
            return Integer.parseInt(textoId.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID del cliente debe ser un número entero válido.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}