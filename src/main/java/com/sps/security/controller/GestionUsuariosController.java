package com.sps.security.controller;

import com.sps.security.model.users.Usuario;
import com.sps.security.service.UsuarioService;
import com.sps.security.util.TableUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    @FXML private StackPane modalFormulario;
    @FXML private ToggleButton btnFiltroTodos;
    @FXML private ToggleButton btnFiltroActivos;
    @FXML private ToggleButton btnFiltroInactivos;
    @FXML private Label lblModalTitulo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    private FilteredList<Usuario> usuariosFiltrados;

    private final UsuarioService usuarioService = new UsuarioService();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private Usuario usuarioSeleccionado = null;
    private final ObservableList<String> roles = FXCollections.observableArrayList(
            "Administrador",
            "Operador",
            "Guardia"
    );

    @FXML
    private void filtrarTodos() {
        usuariosFiltrados.setPredicate(u -> true);
    }

    @FXML
    private void filtrarActivos() {
        usuariosFiltrados.setPredicate(u -> Boolean.TRUE.equals(u.getEstadoActivo()));
    }

    @FXML
    private void filtrarInactivos() {
        usuariosFiltrados.setPredicate(u -> !Boolean.TRUE.equals(u.getEstadoActivo()));
    }

    @FXML
    public void abrirFormularioNuevo() {
        handleLimpiar();
        lblModalTitulo.setText("Nuevo Usuario");
        btnGuardar.setVisible(true);
        btnGuardar.setManaged(true);
        btnActualizar.setVisible(false);
        btnActualizar.setManaged(false);
        btnEliminar.setVisible(false);
        btnEliminar.setManaged(false);
        mostrarFormulario();
    }

    private void mostrarFormularioEdicion() {
        lblModalTitulo.setText("Editar Usuario");
        btnGuardar.setVisible(false);
        btnGuardar.setManaged(false);
        btnActualizar.setVisible(true);
        btnActualizar.setManaged(true);
        btnEliminar.setVisible(true);
        btnEliminar.setManaged(true);
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
        // 1. Enlazar columnas, cargar datos de BD y llenar ComboBox de roles
        configurarTabla();
        cargarUsuarios();
        cmbIdRol.setItems(roles);

        // 2. Configurar grupo de píldoras y FilteredList
        ToggleGroup grupoFiltros = new ToggleGroup();
        btnFiltroTodos.setToggleGroup(grupoFiltros);
        btnFiltroActivos.setToggleGroup(grupoFiltros);
        btnFiltroInactivos.setToggleGroup(grupoFiltros);

        grupoFiltros.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) oldVal.setSelected(true);
        });

        usuariosFiltrados = new FilteredList<>(listaUsuarios, u -> true);
        tblUsuarios.setItems(usuariosFiltrados);

        TableUtils.configurarAlturaAdaptativa(tblUsuarios, 42.0, 40.0, 520.0);

        // 3. Mostrar nombre del rol en lugar del ID numérico
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

        // 4. Celda de Estado usando clases CSS
        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                getStyleClass().removeAll("status-active", "status-inactive");
                if (empty || activo == null) {
                    setText(null);
                } else if (activo) {
                    setText("●  Activo");
                    getStyleClass().add("status-active");
                } else {
                    setText("●  Inactivo");
                    getStyleClass().add("status-inactive");
                }
            }
        });

        // 5. Celda de Acciones (botón lápiz para abrir modal)
        colAcciones.setCellFactory(column -> new TableCell<>() {
            private final Button btnEditar = new Button("✎");
            {
                btnEditar.getStyleClass().add("table-action-btn");
                btnEditar.setTooltip(new Tooltip("Editar usuario"));
                btnEditar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    usuarioSeleccionado = usuario;
                    tblUsuarios.getSelectionModel().select(usuario);
                    cargarFormulario(usuario);
                    mostrarFormularioEdicion();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEditar);
                setAlignment(javafx.geometry.Pos.CENTER);
            }
        });

        // 6. Doble clic en una fila abre el modal de edición
        tblUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tblUsuarios.getSelectionModel().getSelectedItem() != null) {
                usuarioSeleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
                cargarFormulario(usuarioSeleccionado);
                mostrarFormularioEdicion();
            }
        });
    }
    private void configurarTabla() {
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getIdUsuario()));
        colUsername.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        colRol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getIdRol()));
        colEstado.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getEstadoActivo()));
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
        ocultarFormulario();
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
        ocultarFormulario();
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
        ocultarFormulario();
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