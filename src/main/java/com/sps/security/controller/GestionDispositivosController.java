package com.sps.security.controller;

import com.sps.security.dao.impl.ServicioDAOImpl;
import com.sps.security.model.operative.Servicio;
import com.sps.security.model.telemetry.Dispositivo;
import com.sps.security.service.DispositivoService;
import com.sps.security.service.SimuladorAlarmaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class GestionDispositivosController {
    @FXML private TableView<Dispositivo> tablaDispositivos;
    @FXML private TableColumn<Dispositivo,String> columnaId, columnaSerie, columnaMac, columnaIp, columnaEstado;
    @FXML private ComboBox<Servicio> campoServicio;
    @FXML private TextField campoSerie, campoMac, campoIp, campoFabricante;
    @FXML private DatePicker campoFecha;
    @FXML private Button botonGuardar;
    private final DispositivoService service = new DispositivoService();
    private final SimuladorAlarmaService simulador = new SimuladorAlarmaService();
    private final ServicioDAOImpl servicioDAO = new ServicioDAOImpl();
    private Dispositivo seleccionado;

    @FXML public void initialize() {
        columnaId.setCellValueFactory(c -> text(c.getValue().getIdDispositivo()));
        columnaSerie.setCellValueFactory(c -> text(c.getValue().getNumSerie()));
        columnaMac.setCellValueFactory(c -> text(c.getValue().getMacAddress()));
        columnaIp.setCellValueFactory(c -> text(c.getValue().getIpAsignada()));
        columnaEstado.setCellValueFactory(c -> text(Boolean.TRUE.equals(c.getValue().getEstadoActividad()) ? "ACTIVO" : "INACTIVO"));
        campoServicio.setCellFactory(v -> new ListCell<>() { @Override protected void updateItem(Servicio s, boolean empty) { super.updateItem(s,empty); setText(empty || s==null ? null : s.getNombreServicio()+" (#"+s.getIdServicio()+")"); }});
        tablaDispositivos.getSelectionModel().selectedItemProperty().addListener((o,old,value) -> seleccionar(value));
        cargar();
    }
    @FXML private void guardar() {
        try {
            if (campoServicio.getValue() == null) throw new IllegalArgumentException("Debes seleccionar un servicio.");
            Dispositivo d = seleccionado == null ? new Dispositivo(null, campoServicio.getValue().getIdServicio(), campoSerie.getText(), campoMac.getText(), campoIp.getText(), campoFabricante.getText(), campoFecha.getValue(), true) : seleccionado;
            if (seleccionado != null) { d.setIdServicio(campoServicio.getValue().getIdServicio()); d.setNumSerie(campoSerie.getText()); d.setMacAddress(campoMac.getText()); d.setIpAsignada(campoIp.getText()); d.setFabricante(campoFabricante.getText()); d.setFechaInstalacion(campoFecha.getValue()); service.actualizar(d); }
            else service.registrar(d);
            limpiar(); cargarTabla(); mensaje(Alert.AlertType.INFORMATION,"Dispositivo guardado correctamente.");
        } catch (RuntimeException e) { mensaje(Alert.AlertType.ERROR,e.getMessage()); }
    }
    @FXML private void desactivar() { if (seleccionado == null) { mensaje(Alert.AlertType.WARNING,"Selecciona un dispositivo."); return; } try { service.desactivar(seleccionado.getIdDispositivo()); cargarTabla(); limpiar(); } catch(RuntimeException e){mensaje(Alert.AlertType.ERROR,e.getMessage());} }
    @FXML private void nuevo() { limpiar(); }
    @FXML private void actualizar() { cargar(); }
    @FXML private void iniciarSimulador() {
        try {
            simulador.iniciar(10);
            mensaje(Alert.AlertType.INFORMATION,
                    "Simulador iniciado: genera alarmas cada 10 segundos.");
        } catch (RuntimeException e) {
            mensaje(Alert.AlertType.ERROR, e.getMessage());
        }
    }
    @FXML private void detenerSimulador() {
        simulador.detener();
        mensaje(Alert.AlertType.INFORMATION, "Simulador detenido.");
    }
    private void cargar() { campoServicio.getItems().setAll(servicioDAO.obtenerServiciosActivos()); cargarTabla(); }
    private void cargarTabla() { tablaDispositivos.getItems().setAll(new com.sps.security.dao.impl.DispositivoDAOImpl().obtenerTodos()); }
    private void seleccionar(Dispositivo d) { seleccionado=d; if(d==null)return; campoSerie.setText(d.getNumSerie());campoMac.setText(d.getMacAddress());campoIp.setText(d.getIpAsignada());campoFabricante.setText(d.getFabricante());campoFecha.setValue(d.getFechaInstalacion());campoServicio.getItems().stream().filter(s->s.getIdServicio().equals(d.getIdServicio())).findFirst().ifPresent(campoServicio::setValue);botonGuardar.setText("Actualizar dispositivo"); }
    private void limpiar(){seleccionado=null;tablaDispositivos.getSelectionModel().clearSelection();campoSerie.clear();campoMac.clear();campoIp.clear();campoFabricante.clear();campoFecha.setValue(LocalDate.now());campoServicio.setValue(null);botonGuardar.setText("Registrar dispositivo");}
    private SimpleStringProperty text(Object value){return new SimpleStringProperty(value==null?"":String.valueOf(value));}
    private void mensaje(Alert.AlertType type,String message){Alert a=new Alert(type,message,ButtonType.OK);a.setHeaderText(null);a.showAndWait();}
}
