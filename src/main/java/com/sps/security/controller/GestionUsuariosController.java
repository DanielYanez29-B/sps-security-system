package com.sps.security.controller;

import com.sps.security.dao.UsuarioDAO;
import com.sps.security.dao.impl.UsuarioDAOImpl;
import com.sps.security.model.users.Usuario;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class GestionUsuariosController {

    // Componentes de la Tabla
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, Integer> colRol;
    @FXML private TableColumn<Usuario, Boolean> colEstado;

    // Componentes del Formulario
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtIdRol;
    @FXML private CheckBox chkEstadoActivo;

    // Capa de Acceso a Datos y Lista Observable
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private Usuario usuarioSeleccionado = null;

    @FXML
    public void initialize() {
        configurarTabla();
        cargarUsuarios();

        // Escuchar la selección de una fila en la tabla
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                usuarioSeleccionado = newSelection;
                cargarFormulario(newSelection);
            }
        });
    }

    private void configurarTabla() {
        // Mapeo directo mediante Lambdas (sin reflexión ni fallos silenciosos)
        colId.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getIdUsuario()));

        colUsername.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUsername()));

        colEmail.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail()));

        colRol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getIdRol()));

        colEstado.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getEstadoActivo()));

        tblUsuarios.setItems(listaUsuarios);
    }
    private void cargarUsuarios() {
        listaUsuarios.setAll(usuarioDAO.obtenerTodos());
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario(true)) return;

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(txtUsername.getText().trim());
        nuevoUsuario.setEmail(txtEmail.getText().trim());
        nuevoUsuario.setContrasena(txtContrasena.getText().trim());
        nuevoUsuario.setIdRol(Integer.parseInt(txtIdRol.getText().trim()));
        nuevoUsuario.setEstadoActivo(chkEstadoActivo.isSelected());

        if (usuarioDAO.guardar(nuevoUsuario)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario registrado correctamente.");
            cargarUsuarios();
            handleLimpiar();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo registrar el usuario.");
        }
    }

    @FXML
    private void handleActualizar() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un usuario de la tabla.");
            return;
        }

        if (!validarFormulario(false)) return;

        usuarioSeleccionado.setUsername(txtUsername.getText().trim());
        usuarioSeleccionado.setEmail(txtEmail.getText().trim());
        if (!txtContrasena.getText().isBlank()) {
            usuarioSeleccionado.setContrasena(txtContrasena.getText().trim());
        }
        usuarioSeleccionado.setIdRol(Integer.parseInt(txtIdRol.getText().trim()));
        usuarioSeleccionado.setEstadoActivo(chkEstadoActivo.isSelected());

        if (usuarioDAO.actualizar(usuarioSeleccionado)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario actualizado correctamente.");
            cargarUsuarios();
            handleLimpiar();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el usuario.");
        }
    }

    @FXML
    private void handleEliminar() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un usuario de la tabla.");
            return;
        }

        if (usuarioDAO.eliminar(usuarioSeleccionado)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario eliminado correctamente.");
            cargarUsuarios();
            handleLimpiar();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el usuario.");
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

    private void cargarFormulario(Usuario usuario) {
        txtUsername.setText(usuario.getUsername());
        txtEmail.setText(usuario.getEmail());
        txtContrasena.clear(); // Por seguridad no mostramos la contraseña
        txtIdRol.setText(usuario.getIdRol() != null ? usuario.getIdRol().toString() : "");
        chkEstadoActivo.setSelected(usuario.getEstadoActivo() != null && usuario.getEstadoActivo());
    }

    private boolean validarFormulario(boolean esNuevo) {
        if (txtUsername.getText().isBlank() || txtEmail.getText().isBlank() || txtIdRol.getText().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Los campos Username, Email e ID Rol son obligatorios.");
            return false;
        }
        if (esNuevo && txtContrasena.getText().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La contraseña es obligatoria para nuevos usuarios.");
            return false;
        }
        try {
            Integer.parseInt(txtIdRol.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "El ID de Rol debe ser un número entero válido.");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}