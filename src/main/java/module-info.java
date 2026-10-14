module hr.java.garden.projektkucinic {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires jbcrypt;
    requires java.sql;


    opens hr.java.garden.projektkucinic to javafx.fxml;
    exports hr.java.garden.projektkucinic;
    exports hr.java.garden.projektkucinic.controllers;
    opens hr.java.garden.projektkucinic.controllers to javafx.fxml;
}