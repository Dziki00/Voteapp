package com.example.voteapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewUtils {

    private static final double WIDTH = 1200;  // Ustawiona szerokość
    private static final double HEIGHT = 700; // Ustawiona wysokość

    public static void switchView(Stage stage, String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ViewUtils.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
