package com.sps.security.controller;

import com.sps.security.model.enums.TipoElemento;
import com.sps.security.model.supervised.ElementoSupervisado;
import com.sps.security.service.ElementoSupervisadoService;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GestionElementosSupervisadosController {

    private final ElementoSupervisadoService elementoService =
            new ElementoSupervisadoService();

    private ElementoSupervisado elementoSeleccionado;

    // Tabla
    @FXML private TableView<ElementoSupervisado> tblElementos;

    @FXML private TableColumn<ElementoSupervisado, Integer> colId;
    @FXML private TableColumn<ElementoSupervisado, Integer> colServicio;
    @FXML private TableColumn<ElementoSupervisado, String> colTipo;
    @FXML private TableColumn<ElementoSupervisado, String> colIdentificador;
    @FXML private TableColumn<ElementoSupervisado, String> colDescripcion;
    @FXML private TableColumn<ElementoSupervisado, String> colEstado;

    // Formulario
    @FXML private TextField txtIdServicio;
    @FXML private ComboBox<TipoElemento> cmbTipoElemento;
    @FXML private TextField txtIdentificador;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox<String> cmbEstado;

    @FXML
    public void initialize() {

        configurarTabla();

        cmbTipoElemento.setItems(
                FXCollections.observableArrayList(
                        TipoElemento.values()
                )
        );

        cmbEstado.setItems(
                FXCollections.observableArrayList(
                        "ACTIVO", "INACTIVO"
                )
        );

        cmbEstado.setValue("ACTIVO");

        tblElementos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        cargarFormulario(seleccionado);
                    } else {
                        elementoSeleccionado = null;
                    }
                });

        cargarElementos();
    }

    private void configurarTabla() {

        colId.setCellValueFactory(
                dato -> new ReadOnlyObjectWrapper<>(
                        dato.getValue().getIdElemento()
                )
        );

        colServicio.setCellValueFactory(
                dato -> new ReadOnlyObjectWrapper<>(
                        dato.getValue().getIdServicio()
                )
        );

        colTipo.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getTipoElemento() == null
                                ? ""
                                : dato.getValue().getTipoElemento().name()
                )
        );

        colIdentificador.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getIdentificador()
                )
        );

        colDescripcion.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getDescripcion()
                )
        );

        colEstado.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        Boolean.TRUE.equals(
                                dato.getValue().getEstadoActivo()
                        ) ? "ACTIVO" : "INACTIVO"
                )
        );
    }

    private void cargarElementos() {
        try {
            tblElementos.setItems(
                    FXCollections.observableArrayList(
                            elementoService.listarElementos()
                    )
            );
        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar elementos",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleGuardar() {
        try {
            ElementoSupervisado elemento =
                    construirElemento(null);

            elementoService.registrarElemento(elemento);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Elemento supervisado registrado correctamente."
            );

            handleLimpiar();
            cargarElementos();

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

        if (elementoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Aviso",
                    "Selecciona un elemento de la tabla."
            );
            return;
        }

        try {
            ElementoSupervisado elemento =
                    construirElemento(
                            elementoSeleccionado.getIdElemento()
                    );

            elementoService.actualizarElemento(elemento);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Elemento supervisado actualizado correctamente."
            );

            handleLimpiar();
            cargarElementos();

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

        if (elementoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Aviso",
                    "Selecciona un elemento de la tabla."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Estás seguro de eliminar el elemento "
                        + elementoSeleccionado.getIdentificador() + "?"
        );

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        try {
            elementoService.eliminarElemento(elementoSeleccionado);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Elemento supervisado eliminado correctamente."
            );

            handleLimpiar();
            cargarElementos();

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

        txtIdServicio.clear();
        cmbTipoElemento.setValue(null);
        txtIdentificador.clear();
        txtDescripcion.clear();
        cmbEstado.setValue("ACTIVO");

        elementoSeleccionado = null;
        tblElementos.getSelectionModel().clearSelection();
    }

    private ElementoSupervisado construirElemento(Integer id) {

        String textoIdServicio = txtIdServicio.getText().trim();

        if (textoIdServicio.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe indicar el ID del servicio."
            );
        }

        Integer idServicio;

        try {
            idServicio = Integer.valueOf(textoIdServicio);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El ID del servicio debe ser un número entero válido."
            );
        }

        String estado = cmbEstado.getValue();

        Boolean activo = null;

        if ("ACTIVO".equals(estado)) {
            activo = true;
        } else if ("INACTIVO".equals(estado)) {
            activo = false;
        }

        return new ElementoSupervisado(
                id,
                idServicio,
                cmbTipoElemento.getValue(),
                obtenerTexto(txtIdentificador),
                obtenerTexto(txtDescripcion),
                activo
        );
    }

    private String obtenerTexto(TextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }

    private void cargarFormulario(ElementoSupervisado elemento) {

        elementoSeleccionado = elemento;

        txtIdServicio.setText(
                String.valueOf(elemento.getIdServicio())
        );

        cmbTipoElemento.setValue(
                elemento.getTipoElemento()
        );

        txtIdentificador.setText(
                elemento.getIdentificador()
        );

        txtDescripcion.setText(
                elemento.getDescripcion()
        );

        cmbEstado.setValue(
                Boolean.TRUE.equals(elemento.getEstadoActivo())
                        ? "ACTIVO"
                        : "INACTIVO"
        );
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(
                mensaje == null
                        ? "Ha ocurrido un error."
                        : mensaje
        );
        alerta.showAndWait();
    }
}
