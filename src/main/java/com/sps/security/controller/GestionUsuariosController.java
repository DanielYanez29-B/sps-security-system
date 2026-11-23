package com.sps.security.controller;

import com.sps.security.model.users.Usuario;
import com.sps.security.service.UsuarioService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

public class GestionUsuariosController {

    // Componentes FXML de la Tabla
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, Integer> colRol;
    @FXML private TableColumn<Usuario, Boolean> colEstado;
    @FXML private TableColumn<Usuario, Void> colAcciones;

    // Componentes FXML del Formulario
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<String> cmbIdRol;
    @FXML private CheckBox chkEstadoActivo;
    @FXML
    private StackPane modalFormulario;

    // Servicio de Negocio y Lista de Datos
    private final UsuarioService usuarioService = new UsuarioService();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private Usuario usuarioSeleccionado = null;
    private final ObservableList<String> roles = FXCollections.observableArrayList(
            "Administrador",
            "Operador",
            "Guardia"
    );

    @FXML
    public void abrirFormularioNuevo() {
        handleLimpiar();
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

    @FXML
    public void initialize() {
        configurarTabla();
        cargarUsuarios();
        cmbIdRol.setItems(roles);

        // Listener para sincronizar la fila seleccionada de la tabla con el formulario
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                usuarioSeleccionado = newSel;
                cargarFormulario(newSel);
            }
        });
        // Indicador visual con punto de color en la columna Estado
        colEstado.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                    setStyle("");
                } else if (activo) {
                    setText("●  Activo");
                    setStyle("-fx-text-fill: #16A34A; -fx-font-weight: bold;");
                } else {
                    setText("●  Inactivo");
                    setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold;");
                }
            }
        });

        colRol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer idRol, boolean empty) {
                super.updateItem(idRol, empty);
                if (empty || idRol == null) {
                    setText(null);
                } else {
                    String nombreRol = regresarIdRol(idRol);
                    setText(nombreRol != null ? nombreRol : "Sin rol (" + idRol + ")");
                }
            }
        });

        colAcciones.setCellFactory(column -> new TableCell<>() {
            private final Button btnEditar = new Button("✎");

            {
                btnEditar.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: #6366F1;" +
                                "-fx-font-size: 16px;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 2 8 2 8;"
                );
                btnEditar.setTooltip(new Tooltip("Editar usuario"));

                // Efecto hover sencillo
                btnEditar.setOnMouseEntered(e -> btnEditar.setStyle(
                        "-fx-background-color: #EEF2FF;" +
                                "-fx-text-fill: #4F46E5;" +
                                "-fx-font-size: 16px;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 2 8 2 8;"
                ));
                btnEditar.setOnMouseExited(e -> btnEditar.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: #6366F1;" +
                                "-fx-font-size: 16px;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 2 8 2 8;"
                ));

                btnEditar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    usuarioSeleccionado = usuario;
                    tblUsuarios.getSelectionModel().select(usuario);
                    cargarFormulario(usuario);
                    mostrarFormulario();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEditar);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });

// Abrir el modal automáticamente al hacer doble clic en un usuario para editar/eliminar
        tblUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tblUsuarios.getSelectionModel().getSelectedItem() != null) {
                mostrarFormulario();
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
        cmbIdRol.setValue(null);
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
        String rolSeleccionado = cmbIdRol.getValue();
        if (rolSeleccionado == null || rolSeleccionado.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar un rol.");
        }
        return switch (rolSeleccionado) {
            case "Administrador" -> 1;
            case "Operador" -> 2;
            case "Guardia" -> 3;
            default -> throw new IllegalArgumentException("Rol no válido: " + rolSeleccionado);
        };
    }

    private String regresarIdRol(Integer idRol) {
        if (idRol == null) {
            return null;
        }
        return switch (idRol) {
            case 1 -> "Administrador";
            case 2 -> "Operador";
            case 3 -> "Guardia";
            default -> null; // O "Desconocido" si prefieres mostrar algo en el combo
        };
    }

    private void cargarFormulario(Usuario u) {
        txtUsername.setText(u.getUsername());
        txtEmail.setText(u.getEmail());
        txtContrasena.clear(); // Por seguridad no se llena la contraseña existente
        cmbIdRol.setValue(regresarIdRol(u.getIdRol()));
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