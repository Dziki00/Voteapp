package com.example.voteapp.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewUtils {

    private static final double WIDTH = 1200;  // Stała szerokość aplikacji
    private static final double HEIGHT = 700; // Stała wysokość aplikacji

    public static void switchView(Stage stage, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewUtils.class.getResource("/com/example/voteapp/" + fxmlFile));
            Parent root = loader.load();
            setupScene(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Nie można załadować widoku: " + fxmlFile);
        }
    }

    public static <T> void switchViewWithUserId(Stage stage, String fxmlFile, ControllerHandler<T> controllerHandler) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewUtils.class.getResource("/com/example/voteapp/" + fxmlFile));
            Parent root = loader.load();

            // Pobierz kontroler i przekaż dane za pomocą handlera
            T controller = loader.getController();
            controllerHandler.handle(controller);

            setupScene(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Nie można załadować widoku: " + fxmlFile);
        }
    }

    private static void setupScene(Stage stage, Parent root) {
        if (stage.getScene() == null) {
            // Jeśli scena nie istnieje, utwórz nową z ustawionym rozmiarem
            Scene scene = new Scene(root, WIDTH, HEIGHT);
            stage.setScene(scene);
        } else {
            // Jeśli scena istnieje, zmień tylko jej zawartość
            stage.getScene().setRoot(root);
        }

        // Ustaw stałe wymiary i wyłącz możliwość zmiany rozmiaru
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setResizable(false);
        stage.show();
    }

    @FunctionalInterface
    public interface ControllerHandler<T> {
        void handle(T controller);
    }
}
