module com.sps.security {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires bcrypt;


    opens com.sps.security.controller to javafx.fxml;
    opens com.sps.security.model.users to javafx.fxml;
    opens com.sps.security.model.telemetry to javafx.fxml;
    opens com.sps.security.model.enums to javafx.fxml;
    opens com.sps.security.service to javafx.fxml;
    opens com.sps.security to javafx.fxml;
    opens com.sps.security.model.operative to javafx.base;
    opens com.sps.security.model.supervised to javafx.base;
    exports com.sps.security;

}