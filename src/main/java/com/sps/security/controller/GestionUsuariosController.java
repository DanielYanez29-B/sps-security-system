package com.sps.security.controller;

import com.sps.security.model.users.Usuario;
import com.sps.security.service.UsuarioService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GestionUsuariosController {

    // Componentes FXML de la Tabla
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, Integer> colRol;
    @FXML private TableColumn<Usuario, Boolean> colEstado;

    // Componentes FXML del Formulario
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtIdRol;
    @FXML private CheckBox chkEstadoActivo;

    // Servicio de Negocio y Lista de Datos
    private final UsuarioService usuarioService = new UsuarioService();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private Usuario usuarioSeleccionado = null;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarUsuarios();

        // Listener para sincronizar la fila seleccionada de la tabla con el formulario
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                usuarioSeleccionado = newSel;
                cargarFormulario(newSel);
            }
        });
    }

    private void configurarTabla() {
        // Mapeo seguro mediante Lambdas (evita que la tabla quede en blanco)
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getIdUsuario()));
        colUsername.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colRol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getIdRol()));
        colEstado.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEstadoActivo()));

        tblUsuarios.setItems(listaUsuarios);
    }

    private void cargarUsuarios() {
        listaUsuarios.setAll(usuarioService.listarUsuarios());
    }

    @FXML
    private void handleGuardar() {
        try {
            Usuario nuevoUsuario = construirUsuarioDesdeFormulario();
            usuarioService.registrarUsuario(nuevoUsuario);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario registrado correctamente.");
            cargarUsuarios();
            handleLimpiar();
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación de Negocio", e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un usuario de la tabla.");
            return;
        }

        try {
            usuarioSeleccionado.setUsername(txtUsername.getText().trim());
            usuarioSeleccionado.setEmail(txtEmail.getText().trim());

            // Solo actualiza la contraseña si se ingresó algo en el campo
            if (!txtContrasena.getText().isBlank()) {
                usuarioSeleccionado.setContrasena(txtContrasena.getText().trim());
            }

            usuarioSeleccionado.setIdRol(parsearIdRol());
            usuarioSeleccionado.setEstadoActivo(chkEstadoActivo.isSelected());

            usuarioService.actualizarUsuario(usuarioSeleccionado);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario actualizado correctamente.");
            cargarUsuarios();
            handleLimpiar();
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación de Negocio", e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un usuario de la tabla.");
            return;
        }

        try {
            usuarioService.eliminarUsuario(usuarioSeleccionado);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario eliminado correctamente.");
            cargarUsuarios();
            handleLimpiar();
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar() {
        usuarioSeleccionado = null;
        txtUsername.clear();
        txtEmail.clear();
        txtContrasena.clear();
        txtIdRol.clear();
        chkEstadoActivo.setSelected(true);
        tblUsuarios.getSelectionModel().clearSelection();
    }

    private Usuario construirUsuarioDesdeFormulario() {
        Usuario u = new Usuario();
        u.setUsername(txtUsername.getText().trim());
        u.setEmail(txtEmail.getText().trim());
        u.setContrasena(txtContrasena.getText().trim());
        u.setIdRol(parsearIdRol());
        u.setEstadoActivo(chkEstadoActivo.isSelected());
        return u;
    }

    private Integer parsearIdRol() {
        try {
            return Integer.parseInt(txtIdRol.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID de Rol debe ser un número entero válido.");
        }
    }

    private void cargarFormulario(Usuario u) {
        txtUsername.setText(u.getUsername());
        txtEmail.setText(u.getEmail());
        txtContrasena.clear(); // Por seguridad no se llena la contraseña existente
        txtIdRol.setText(u.getIdRol() != null ? u.getIdRol().toString() : "");
        chkEstadoActivo.setSelected(u.getEstadoActivo() != null && u.getEstadoActivo());
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}