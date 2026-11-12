package com.sps.security.controller;

import com.sps.security.model.users.Usuario;
import com.sps.security.service.access_service.AuthService;
import com.sps.security.util.NavigationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserRole;

    // ToggleButtons from the FXML
    @FXML
    private ToggleButton btnInicio;
    @FXML
    private ToggleButton btnUsuarios;
    @FXML
    private ToggleButton btnGuardias;
    @FXML
    private ToggleButton btnServicios;
    @FXML
    private ToggleButton btnMonitoreo;
    @FXML
    private ToggleButton btnAlertas;
    @FXML
    private ToggleButton btnReportes;
    @FXML
    private ToggleButton btnConfig;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Usuario u = AuthService.getCurrentUser();
        if (u != null) {
            lblUserName.setText(u.getUsername());
            // Assuming Rol handling is better, but keeping your switch for now
            lblUserRole.setText(switch (u.getIdRol()) {
                case 1 -> "Administrador";
                case 2 -> "Operador";
                case 3 -> "Guardia";
                default -> "Desconocido";
            });
        }
        verificarPermisosParaVistaDePestanas();
    }

    @FXML
    public void onLogout() {
        if (confirmacionCerrarSesion()) {
            AuthService.limpiarSesion();
            NavigationService.getInstance().cargarVista("login-view.fxml");
        }
    }

    @FXML
    public void onInicio() {
        // NavigationService.getInstance().cargarVista("inicio-view.fxml");
    }

    @FXML
    public void onUsuarios() {
        NavigationService.getInstance().cargarVista("usuarios-view.fxml");
    }

    @FXML
    public void onGuardias() {
        // NavigationService.getInstance().cargarVista("guardias-view.fxml");
    }

    @FXML
    public void onServicios() {
        // NavigationService.getInstance().cargarVista("servicios-view.fxml");
    }

    @FXML
    public void onMonitoreo() {
        // NavigationService.getInstance().cargarVista("monitoreo-view.fxml");
    }

    @FXML
    public void onAlertas() {
        // NavigationService.getInstance().cargarVista("alertas-view.fxml");
    }

    @FXML
    public void onReportes() {
        // NavigationService.getInstance().cargarVista("reportes-view.fxml");
    }

    @FXML
    public void onConfig() {
        // NavigationService.getInstance().cargarVista("config-view.fxml");
    }

    private void verificarPermisosParaVistaDePestanas() {
        // Inicio/Dashboard
        boolean canSeeDashboard = AuthService.tienePermiso("DASHBOARD_VER");
        btnInicio.setVisible(canSeeDashboard);
        btnInicio.setManaged(canSeeDashboard);

        // Usuarios (Admin only)
        boolean canSeeUsers = AuthService.tienePermiso("USUARIO_VER") || AuthService.tienePermiso("USUARIO_GESTION");
        btnUsuarios.setVisible(canSeeUsers);
        btnUsuarios.setManaged(canSeeUsers);

        // Guardias
        boolean canSeeGuardias = AuthService.tienePermiso("GUARDIA_VER") || AuthService.tienePermiso("GUARDIA_GESTION");
        btnGuardias.setVisible(canSeeGuardias);
        btnGuardias.setManaged(canSeeGuardias);

        // Servicios / Asignaciones
        boolean canSeeServicios = AuthService.tienePermiso("SERVICIO_VER") || AuthService.tienePermiso("SERVICIO_GESTION");
        btnServicios.setVisible(canSeeServicios);
        btnServicios.setManaged(canSeeServicios);

        // Monitoreo (Dispositivos / Elementos)
        boolean canSeeMonitoreo = AuthService.tienePermiso("DISPOSITIVO_VER") || AuthService.tienePermiso("ELEMENTO_VER");
        btnMonitoreo.setVisible(canSeeMonitoreo);
        btnMonitoreo.setManaged(canSeeMonitoreo);

        // Alertas
        boolean canSeeAlertas = AuthService.tienePermiso("ALARMA_VER");
        btnAlertas.setVisible(canSeeAlertas);
        btnAlertas.setManaged(canSeeAlertas);

        // Reportes (Assuming Dashboard/Reports overlap or require specific view permissions not explicitly in matrix, tying to Dashboard/Alerts for now)
        boolean canSeeReportes = AuthService.tienePermiso("DASHBOARD_VER"); // Or add a REPORTE_VER permission
        btnReportes.setVisible(canSeeReportes);
        btnReportes.setManaged(canSeeReportes);

        // Configuración (Roles, Dispositivo Config)
        boolean canSeeConfig = AuthService.tienePermiso("ROL_GESTION") || AuthService.tienePermiso("DISPOSITIVO_CONFIG");
        btnConfig.setVisible(canSeeConfig);
        btnConfig.setManaged(canSeeConfig);
    }

    private boolean confirmacionCerrarSesion(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cierre de Sesión");
        alert.setHeaderText("¿Estás seguro de que quieres cerrar la sesión?");
        alert.setContentText("Selecciona 'Aceptar' para confirmar o 'Cancelar' para volver atrás.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }
}