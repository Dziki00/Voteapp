package com.example.voteapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setTitle("Aplikacja do głosowania");
        stage.setScene(scene);
        stage.setResizable(false); // Wyłączenie zmiany rozmiaru okna
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
