package com.example.voteapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminController {

    @FXML
    private Button managePollsButton;

    @FXML
    private Button manageUsersButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        // Obsługa przycisku zarządzania ankietami
        managePollsButton.setOnAction(event -> {
            System.out.println("Przejście do zarządzania ankietami");
            // Dodaj logikę przejścia do widoku zarządzania ankietami
        });

        // Obsługa przycisku zarządzania użytkownikami
        manageUsersButton.setOnAction(event -> {
            System.out.println("Przejście do zarządzania użytkownikami");
            // Dodaj logikę przejścia do widoku zarządzania użytkownikami
        });

        // Obsługa przycisku wylogowania
        logoutButton.setOnAction(event -> {
            System.out.println("Wylogowanie użytkownika");
            ViewUtils.switchView((Stage) logoutButton.getScene().getWindow(), "start-view.fxml");
        });
    }
}
