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
            ViewUtils.switchView((Stage) managePollsButton.getScene().getWindow(), "manage-polls-view.fxml");
        });

        // Obsługa przycisku zarządzania użytkownikami
        manageUsersButton.setOnAction(event -> {
            System.out.println("Przejście do zarządzania użytkownikami");
            ViewUtils.switchView((Stage) manageUsersButton.getScene().getWindow(), "manage-users-view.fxml");
        });

        // Obsługa przycisku wylogowania
        logoutButton.setOnAction(event -> {
            System.out.println("Wylogowanie użytkownika");
            ViewUtils.switchView((Stage) logoutButton.getScene().getWindow(), "start-view.fxml");
        });
    }
}
