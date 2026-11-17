package com.sps.security.controller;

import com.sps.security.model.enums.EstadoAlarma;
import com.sps.security.model.enums.NivelPrioridad;
import com.sps.security.model.telemetry.Alarma;
import com.sps.security.service.AlarmaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GestionAlarmasController {
    @FXML private TableView<Alarma> tablaAlarmas;
    @FXML private TableColumn<Alarma,String> columnaId,columnaDispositivo,columnaFecha,columnaTipo,columnaPrioridad,columnaEstado;
    @FXML private DatePicker filtroDesde,filtroHasta;
    @FXML private ComboBox<NivelPrioridad> filtroPrioridad;
    @FXML private ComboBox<EstadoAlarma> filtroEstado;
    private final AlarmaService service = new AlarmaService();
    @FXML public void initialize() { columnaId.setCellValueFactory(c->text(c.getValue().getIdAlarma()));columnaDispositivo.setCellValueFactory(c->text(c.getValue().getIdDispositivo()));columnaFecha.setCellValueFactory(c->text(c.getValue().getFechaHora()));columnaTipo.setCellValueFactory(c->text(c.getValue().getTipoAlarma()));columnaPrioridad.setCellValueFactory(c->text(c.getValue().getNivelPrioridad()));columnaEstado.setCellValueFactory(c->text(c.getValue().getEstado()));filtroPrioridad.getItems().setAll(NivelPrioridad.values());filtroEstado.getItems().setAll(EstadoAlarma.values());cargar(); }
    @FXML private void filtrar(){try{LocalDate d=filtroDesde.getValue(),h=filtroHasta.getValue();tablaAlarmas.getItems().setAll(service.filtrar(d==null?null:d.atStartOfDay(),h==null?null:h.plusDays(1).atStartOfDay().minusNanos(1),null,filtroPrioridad.getValue(),filtroEstado.getValue()));}catch(RuntimeException e){mensaje(Alert.AlertType.ERROR,e.getMessage());}}
    @FXML private void limpiarFiltros(){filtroDesde.setValue(null);filtroHasta.setValue(null);filtroPrioridad.setValue(null);filtroEstado.setValue(null);cargar();}
    @FXML private void marcarAtendida(){cambiar(EstadoAlarma.ATENDIDA);}
    @FXML private void marcarFalsa(){cambiar(EstadoAlarma.FALSA_ALARMA);}
    private void cambiar(EstadoAlarma estado){Alarma a=tablaAlarmas.getSelectionModel().getSelectedItem();if(a==null){mensaje(Alert.AlertType.WARNING,"Selecciona una alarma.");return;}try{service.cambiarEstado(a.getIdAlarma(),estado);cargar();}catch(RuntimeException e){mensaje(Alert.AlertType.ERROR,e.getMessage());}}
    private void cargar(){tablaAlarmas.getItems().setAll(service.listar());}
    private SimpleStringProperty text(Object v){return new SimpleStringProperty(v==null?"":String.valueOf(v));}
    private void mensaje(Alert.AlertType t,String m){Alert a=new Alert(t,m,ButtonType.OK);a.setHeaderText(null);a.showAndWait();}
}
