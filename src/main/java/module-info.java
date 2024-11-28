module com.example.voteapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires spring.security.crypto;

    opens com.example.voteapp to javafx.fxml;
    exports com.example.voteapp;
    exports com.example.voteapp.controllers;
    opens com.example.voteapp.controllers to javafx.fxml;
    exports com.example.voteapp.utils;
    opens com.example.voteapp.utils to javafx.fxml;
}