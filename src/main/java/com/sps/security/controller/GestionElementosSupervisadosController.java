package com.sps.security.controller;

import com.sps.security.model.enums.TipoElemento;
import com.sps.security.model.supervised.ElementoSupervisado;
import com.sps.security.service.ElementoSupervisadoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.Optional;

public class GestionElementosSupervisadosController {

    @FXML
    private TextField campoIdServicio;

    @FXML
    private ComboBox<TipoElemento> comboTipoElemento;

    @FXML
    private TextField campoIdentificador;

    @FXML
    private TextField campoDescripcion;

    @FXML
    private ComboBox<String> comboEstado;

    @FXML
    private Button botonGuardar;

    @FXML
    private TextField campoFiltroServicio;

    @FXML
    private TableView<ElementoSupervisado> tablaElementos;

    @FXML
    private TableColumn<ElementoSupervisado, Integer> columnaId;

    @FXML
    private TableColumn<ElementoSupervisado, Integer> columnaIdServicio;

    @FXML
    private TableColumn<ElementoSupervisado, TipoElemento> columnaTipo;

    @FXML
    private TableColumn<ElementoSupervisado, String> columnaIdentificador;

    @FXML
    private TableColumn<ElementoSupervisado, String> columnaDescripcion;

    @FXML
    private TableColumn<ElementoSupervisado, String> columnaEstado;

    private final ElementoSupervisadoService elementoService;
    private final ObservableList<ElementoSupervisado> listaElementos;
    private ElementoSupervisado elementoSeleccionado;
    @FXML private StackPane modalFormulario;
    @FXML private Label lblModalTitulo;

    public GestionElementosSupervisadosController() {
        this.elementoService = new ElementoSupervisadoService();
        this.listaElementos = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        comboTipoElemento.setItems(FXCollections.observableArrayList(TipoElemento.values()));

        comboEstado.setItems(FXCollections.observableArrayList("ACTIVO", "INACTIVO"));
        comboEstado.setValue("ACTIVO");

        configurarColumnas();
        tablaElementos.setItems(listaElementos);

        tablaElementos.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, actual) -> cargarElementoEnFormulario(actual)
        );
        tablaElementos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tablaElementos.getSelectionModel().getSelectedItem() != null) {
                lblModalTitulo.setText("Editar Elemento Supervisado");
                mostrarFormulario();
            }
        });

        actualizarTabla();
    }

    @FXML
    public void abrirFormularioNuevo() {
        limpiarFormulario();
        lblModalTitulo.setText("Nuevo Elemento Supervisado");
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
        columnaId.setCellValueFactory(new PropertyValueFactory<>("idElemento"));
        columnaIdServicio.setCellValueFactory(new PropertyValueFactory<>("idServicio"));
        columnaTipo.setCellValueFactory(new PropertyValueFactory<>("tipoElemento"));
        columnaIdentificador.setCellValueFactory(new PropertyValueFactory<>("identificador"));
        columnaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnaEstado.setCellValueFactory(cellData -> {
            Boolean activo = cellData.getValue().getEstadoActivo();
            return new SimpleStringProperty(Boolean.TRUE.equals(activo) ? "ACTIVO" : "INACTIVO");
        });
    }

    @FXML
    private void guardarElemento() {
        try {
            Integer idServicio = parsearEntero(campoIdServicio.getText(), "Debe ingresar un ID de servicio válido.");
            TipoElemento tipo = comboTipoElemento.getValue();
            String identificador = campoIdentificador.getText();
            String descripcion = campoDescripcion.getText() != null && !campoDescripcion.getText().isBlank()
                    ? campoDescripcion.getText().trim()
                    : null;
            Boolean estadoActivo = "ACTIVO".equals(comboEstado.getValue());

            if (elementoSeleccionado == null) {
                ElementoSupervisado nuevoElemento = new ElementoSupervisado();
                nuevoElemento.setIdServicio(idServicio);
                nuevoElemento.setTipoElemento(tipo);
                nuevoElemento.setIdentificador(identificador);
                nuevoElemento.setDescripcion(descripcion);
                nuevoElemento.setEstadoActivo(estadoActivo);

                elementoService.registrarElemento(nuevoElemento);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso", "El elemento supervisado se registró correctamente.");
            } else {
                elementoSeleccionado.setIdServicio(idServicio);
                elementoSeleccionado.setTipoElemento(tipo);
                elementoSeleccionado.setIdentificador(identificador);
                elementoSeleccionado.setDescripcion(descripcion);
                elementoSeleccionado.setEstadoActivo(estadoActivo);

                elementoService.actualizarElemento(elementoSeleccionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización exitosa", "El elemento supervisado se actualizó correctamente.");
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
    private void nuevoElemento() {
        limpiarFormulario();
    }

    @FXML
    private void activarElemento() {
        cambiarEstadoElemento(true);
    }

    @FXML
    private void desactivarElemento() {
        cambiarEstadoElemento(false);
    }

    @FXML
    private void eliminarElemento() {
        if (elementoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Selecciona un elemento de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas eliminar el elemento \"" + elementoSeleccionado.getIdentificador() + "\"?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                elementoService.eliminarElemento(elementoSeleccionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Elemento eliminado", "El elemento supervisado fue eliminado correctamente.");
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
    private void filtrarPorServicio() {
        String filtro = campoFiltroServicio.getText();
        if (filtro == null || filtro.isBlank()) {
            actualizarTabla();
            return;
        }

        try {
            Integer idServicio = parsearEntero(filtro, "El ID de servicio para filtrar debe ser un número entero.");
            List<ElementoSupervisado> elementos = elementoService.listarElementosPorServicio(idServicio);
            listaElementos.setAll(elementos);
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Filtro inválido", e.getMessage());
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al filtrar", e.getMessage());
        }
    }

    @FXML
    private void actualizarTabla() {
        try {
            campoFiltroServicio.clear();
            List<ElementoSupervisado> elementos = elementoService.listarElementos();
            listaElementos.setAll(elementos);
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar datos", "No se pudieron obtener los elementos: " + e.getMessage());
        }
    }

    private void cambiarEstadoElemento(boolean activo) {
        if (elementoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Selecciona un elemento de la tabla.");
            return;
        }

        try {
            elementoSeleccionado.setEstadoActivo(activo);
            elementoService.actualizarElemento(elementoSeleccionado);
            String textoEstado = activo ? "ACTIVO" : "INACTIVO";
            mostrarAlerta(Alert.AlertType.INFORMATION, "Estado actualizado", "El elemento ahora está en estado " + textoEstado + ".");
            limpiarFormulario();
            actualizarTabla();
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", e.getMessage());
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al actualizar estado", e.getMessage());
        }
    }

    private void cargarElementoEnFormulario(ElementoSupervisado elemento) {
        this.elementoSeleccionado = elemento;

        if (elemento == null) {
            botonGuardar.setText("Registrar elemento");
            return;
        }

        campoIdServicio.setText(elemento.getIdServicio() != null ? String.valueOf(elemento.getIdServicio()) : "");
        comboTipoElemento.setValue(elemento.getTipoElemento());
        campoIdentificador.setText(elemento.getIdentificador());
        campoDescripcion.setText(elemento.getDescripcion() != null ? elemento.getDescripcion() : "");
        comboEstado.setValue(Boolean.TRUE.equals(elemento.getEstadoActivo()) ? "ACTIVO" : "INACTIVO");
        botonGuardar.setText("Actualizar elemento");
    }

    private void limpiarFormulario() {
        elementoSeleccionado = null;
        campoIdServicio.clear();
        comboTipoElemento.setValue(null);
        campoIdentificador.clear();
        campoDescripcion.clear();
        comboEstado.setValue("ACTIVO");
        botonGuardar.setText("Registrar elemento");
        tablaElementos.getSelectionModel().clearSelection();
    }

    private Integer parsearEntero(String texto, String mensajeError) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensajeError);
        }
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensajeError);
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