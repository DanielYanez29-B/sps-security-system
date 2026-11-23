
package com.sps.security.controller;

import com.sps.security.model.operative.Asignacion;
import com.sps.security.model.operative.Guardia;
import com.sps.security.model.operative.Jornada;
import com.sps.security.model.operative.Servicio;
import com.sps.security.service.AsignacionService;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import java.io.IOException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestionAsignacionesController {

    @FXML
    private ComboBox<Opcion> campoIdGuardia;

    @FXML
    private ComboBox<Opcion> campoIdServicio;

    @FXML
    private ComboBox<Opcion> campoIdJornada;

    @FXML
    private DatePicker campoFechaAsignacion;

    @FXML
    private DatePicker filtroFecha;

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
    @FXML private StackPane modalFormulario;
    @FXML private Label lblModalTitulo;

    private final AsignacionService asignacionService =
            new AsignacionService();

    private final List<Asignacion> todasLasAsignaciones =
            new ArrayList<>();

    private Asignacion asignacionSeleccionada;

    private record Opcion(
            Integer id,
            String descripcion) {

        @Override
        public String toString() {
            return descripcion;
        }
    }

    @FXML
    public void initialize() {

        columnaId.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue()
                                        .getIdAsignacion()
                        )
                )
        );

        columnaGuardia.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue()
                                        .getIdGuardia()
                        )
                )
        );

        columnaServicio.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue()
                                        .getIdServicio()
                        )
                )
        );

        columnaJornada.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.valueOf(
                                data.getValue()
                                        .getIdJornada()
                        )
                )
        );

        columnaFecha.setCellValueFactory(data -> {

            LocalDate fecha =
                    data.getValue()
                            .getFechaAsignacion();

            return new SimpleStringProperty(
                    fecha == null
                            ? ""
                            : fecha.toString()
            );
        });

        tablaAsignaciones.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, anterior, actual) -> {

                            if (actual != null) {
                                seleccionarAsignacion(actual);
                            }
                        }
                );

        filtroFecha.valueProperty()
                .addListener(
                        (observable, anterior, actual) ->
                                aplicarFiltro()
                );

        campoFechaAsignacion.setValue(
                LocalDate.now()
        );

        cargarDatos();

        tablaAsignaciones.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tablaAsignaciones.getSelectionModel().getSelectedItem() != null) {
                lblModalTitulo.setText("Editar Asignación");
                botonGuardar.setText("Actualizar asignación");
                mostrarFormulario();
            }
        });
    }

    @FXML
    public void abrirFormularioNuevo() {
        limpiarFormulario();
        lblModalTitulo.setText("Nueva Asignación");
        botonGuardar.setText("Guardar");
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
    private void abrirGestionJornadas() {
        // Ajusta la ruta al archivo FXML real de tus jornadas
        abrirVentanaModal(
                "/com/sps/security/jornadas-view.fxml",
                "Administrar Jornadas"
        );
    }

    @FXML
    private void abrirGestionUsuarios() {
        // Ajusta la ruta al archivo FXML real de tus usuarios/guardias
        abrirVentanaModal(
                "/com/sps/security/usuarios-view.fxml",
                "Administrar Usuarios"
        );
    }

    private void abrirVentanaModal(String rutaFxml, String titulo) {
        try {
            // Guardamos las selecciones actuales para no perderlas al recargar
            Opcion guardiaPrevio = campoIdGuardia.getValue();
            Opcion servicioPrevio = campoIdServicio.getValue();
            Opcion jornadaPrevia = campoIdJornada.getValue();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaAsignaciones.getScene().getWindow());

            // Espera hasta que el usuario cierre la ventana secundaria
            stage.showAndWait();

            // Recarga los ComboBox con los nuevos registros creados
            cargarOpciones();

            // Restaura las selecciones que el usuario ya tenía elegidas
            if (guardiaPrevio != null) seleccionarOpcion(campoIdGuardia, guardiaPrevio.id());
            if (servicioPrevio != null) seleccionarOpcion(campoIdServicio, servicioPrevio.id());
            if (jornadaPrevia != null) seleccionarOpcion(campoIdJornada, jornadaPrevia.id());

        } catch (IOException e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de navegación",
                    "No se pudo abrir la ventana: " + titulo + "\n" + e.getMessage()
            );
        }
    }

    private void cargarDatos() {

        try {
            cargarOpciones();
            cargarAsignaciones();

        } catch (RuntimeException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al cargar datos",
                    e.getMessage()
            );
        }
    }

    private void cargarOpciones() {

        List<Opcion> guardias =
                asignacionService.listarGuardiasActivos()
                        .stream()
                        .map(this::crearOpcionGuardia)
                        .toList();

        List<Opcion> servicios =
                asignacionService.listarServiciosActivos()
                        .stream()
                        .map(this::crearOpcionServicio)
                        .toList();

        List<Opcion> jornadas =
                asignacionService.listarJornadasActivas()
                        .stream()
                        .map(this::crearOpcionJornada)
                        .toList();

        campoIdGuardia.getItems().setAll(guardias);
        campoIdServicio.getItems().setAll(servicios);
        campoIdJornada.getItems().setAll(jornadas);
    }

    private Opcion crearOpcionGuardia(
            Guardia guardia) {

        return new Opcion(
                guardia.getIdGuardia(),
                guardia.toString()
                        + " (#"
                        + guardia.getIdGuardia()
                        + ")"
        );
    }

    private Opcion crearOpcionServicio(
            Servicio servicio) {

        return new Opcion(
                servicio.getIdServicio(),
                servicio.getNombreServicio()
                        + " (#"
                        + servicio.getIdServicio()
                        + ")"
        );
    }

    private Opcion crearOpcionJornada(
            Jornada jornada) {

        return new Opcion(
                jornada.getIdJornada(),
                jornada.getNombre()
                        + " (#"
                        + jornada.getIdJornada()
                        + ")"
        );
    }

    @FXML
    private void guardarAsignacion() {

        try {

            Integer idGuardia =
                    obtenerIdSeleccionado(
                            campoIdGuardia,
                            "guardia"
                    );

            Integer idServicio =
                    obtenerIdSeleccionado(
                            campoIdServicio,
                            "servicio"
                    );

            Integer idJornada =
                    obtenerIdSeleccionado(
                            campoIdJornada,
                            "jornada"
                    );

            LocalDate fecha =
                    campoFechaAsignacion.getValue();

            if (fecha == null) {
                throw new IllegalArgumentException(
                        "Debes seleccionar una fecha."
                );
            }

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

            limpiarFormulario();
            cargarAsignaciones();
            ocultarFormulario();

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo guardar",
                    e.getMessage()
            );
        }
    }

    private Integer obtenerIdSeleccionado(
            ComboBox<Opcion> combo,
            String nombreCampo) {

        Opcion opcion =
                combo.getValue();

        if (opcion == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un "
                            + nombreCampo + "."
            );
        }

        return opcion.id();
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

        Alert confirmacion =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmacion.setTitle(
                "Confirmar eliminación"
        );

        confirmacion.setHeaderText(
                "¿Deseas eliminar esta asignación?"
        );

        confirmacion.setContentText(
                "Esta acción eliminará el registro "
                        + "de la base de datos."
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

            limpiarFormulario();
            cargarAsignaciones();
            ocultarFormulario();

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
        cargarDatos();
    }

    @FXML
    private void limpiarFiltro() {
        filtroFecha.setValue(null);
    }

    private void seleccionarAsignacion(
            Asignacion asignacion) {

        asignacionSeleccionada = asignacion;

        seleccionarOpcion(
                campoIdGuardia,
                asignacion.getIdGuardia()
        );

        seleccionarOpcion(
                campoIdServicio,
                asignacion.getIdServicio()
        );

        seleccionarOpcion(
                campoIdJornada,
                asignacion.getIdJornada()
        );

        campoFechaAsignacion.setValue(
                asignacion.getFechaAsignacion()
        );

        botonGuardar.setText(
                "Actualizar asignación"
        );
    }

    private void seleccionarOpcion(
            ComboBox<Opcion> combo,
            Integer id) {

        combo.getItems()
                .stream()
                .filter(opcion ->
                        opcion.id().equals(id)
                )
                .findFirst()
                .ifPresentOrElse(
                        combo::setValue,
                        () -> combo.setValue(null)
                );
    }

    private void cargarAsignaciones() {

        try {

            List<Asignacion> asignaciones =
                    asignacionService.listarAsignaciones();

            todasLasAsignaciones.clear();
            todasLasAsignaciones.addAll(
                    asignaciones
            );

            aplicarFiltro();

        } catch (RuntimeException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al consultar asignaciones",
                    e.getMessage()
            );
        }
    }

    private void aplicarFiltro() {

        LocalDate fecha =
                filtroFecha.getValue();

        List<Asignacion> resultado =
                todasLasAsignaciones
                        .stream()
                        .filter(asignacion ->
                                fecha == null
                                        || fecha.equals(
                                        asignacion.getFechaAsignacion()
                                )
                        )
                        .toList();

        tablaAsignaciones.getItems()
                .setAll(resultado);

        if (asignacionSeleccionada != null
                && !resultado.contains(
                asignacionSeleccionada
        )) {

            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {

        tablaAsignaciones.getSelectionModel()
                .clearSelection();

        asignacionSeleccionada = null;

        campoIdGuardia.setValue(null);
        campoIdServicio.setValue(null);
        campoIdJornada.setValue(null);

        campoFechaAsignacion.setValue(
                LocalDate.now()
        );

        botonGuardar.setText(
                "Registrar asignación"
        );
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
