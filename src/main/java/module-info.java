module com.sps.security {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens com.sps.security.controller to javafx.fxml;
    opens com.sps.security.model.users to javafx.fxml;
    opens com.sps.security to javafx.fxml;
    exports com.sps.security;

}