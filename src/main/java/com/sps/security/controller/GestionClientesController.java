package com.sps.security.controller;

import com.sps.security.model.users.Cliente;
import com.sps.security.service.ClienteService;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class GestionClientesController {

    private final ClienteService clienteService = new ClienteService();

    private Cliente clienteSeleccionado;

    // Tabla de clientes
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colRfc;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colEmail;
    @FXML private TableColumn<Cliente, String> colCiudad;

    // Campos del formulario
    @FXML private TextField txtNombre;
    @FXML private TextField txtRfc;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCalle;
    @FXML private TextField txtNumExt;
    @FXML private TextField txtColonia;
    @FXML private TextField txtCiudad;
    @FXML private TextField txtEstado;
    @FXML private TextField txtCp;

    @FXML
    public void initialize() {
        configurarTabla();

        tblClientes.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        cargarFormulario(seleccionado);
                    } else {
                        clienteSeleccionado = null;
                    }
                });

        cargarClientes();
    }

    private void configurarTabla() {

        colId.setCellValueFactory(
                dato -> new ReadOnlyObjectWrapper<>(
                        dato.getValue().getIdcliente()
                )
        );

        colNombre.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getNombre()
                )
        );

        colRfc.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getRfc()
                )
        );

        colTelefono.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getTelefono()
                )
        );

        colEmail.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getEmail()
                )
        );

        colCiudad.setCellValueFactory(
                dato -> new ReadOnlyStringWrapper(
                        dato.getValue().getCiudad()
                )
        );
    }

    private void cargarClientes() {
        try {
            tblClientes.setItems(
                    FXCollections.observableArrayList(
                            clienteService.listarClientes()
                    )
            );
        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al cargar clientes",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleGuardar() {
        try {
            Cliente cliente = construirCliente(null);

            clienteService.registrarCliente(cliente);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Cliente registrado correctamente."
            );

            handleLimpiar();
            cargarClientes();

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

        if (clienteSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Aviso",
                    "Selecciona un cliente de la tabla."
            );
            return;
        }

        try {
            Cliente cliente = construirCliente(
                    clienteSeleccionado.getIdcliente()
            );

            clienteService.actualizarCliente(cliente);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Cliente actualizado correctamente."
            );

            handleLimpiar();
            cargarClientes();

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

        if (clienteSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Aviso",
                    "Selecciona un cliente de la tabla."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Estás seguro de eliminar al cliente "
                        + clienteSeleccionado.getNombre() + "?"
        );

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        try {
            clienteService.eliminarCliente(clienteSeleccionado);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Cliente eliminado correctamente."
            );

            handleLimpiar();
            cargarClientes();

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

        txtNombre.clear();
        txtRfc.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtCalle.clear();
        txtNumExt.clear();
        txtColonia.clear();
        txtCiudad.clear();
        txtEstado.clear();
        txtCp.clear();

        clienteSeleccionado = null;
        tblClientes.getSelectionModel().clearSelection();
    }

    private Cliente construirCliente(Integer id) {

        return new Cliente(
                id,
                obtenerTexto(txtNombre),
                obtenerTexto(txtRfc),
                obtenerTexto(txtTelefono),
                obtenerTexto(txtEmail),
                obtenerTexto(txtCalle),
                obtenerTexto(txtNumExt),
                obtenerTexto(txtColonia),
                obtenerTexto(txtCiudad),
                obtenerTexto(txtEstado),
                obtenerTexto(txtCp)
        );
    }

    private String obtenerTexto(TextField campo) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? null : texto;
    }

    private void cargarFormulario(Cliente cliente) {

        clienteSeleccionado = cliente;

        txtNombre.setText(cliente.getNombre());
        txtRfc.setText(cliente.getRfc());
        txtTelefono.setText(cliente.getTelefono());
        txtEmail.setText(cliente.getEmail());
        txtCalle.setText(cliente.getCalle());
        txtNumExt.setText(cliente.getNum_ext());
        txtColonia.setText(cliente.getColonia());
        txtCiudad.setText(cliente.getCiudad());
        txtEstado.setText(cliente.getEstado());
        txtCp.setText(cliente.getCp());
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
