package com.example.voteapp.controllers;

import com.example.voteapp.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class UserController {

    @FXML
    private Button voteButton;

    @FXML
    private Button checkVotesButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        // Obsługa przycisku "Oddaj głos"
        voteButton.setOnAction(event -> {
            System.out.println("Przejście do oddawania głosów.");
            ViewUtils.switchView((Stage) voteButton.getScene().getWindow(), "vote-view.fxml");
        });

        // Obsługa przycisku "Sprawdź oddane głosy"
        checkVotesButton.setOnAction(event -> {
            System.out.println("Przejście do sprawdzania głosów.");
            ViewUtils.switchView((Stage) checkVotesButton.getScene().getWindow(), "votes-view.fxml");
        });

        // Obsługa przycisku "Wyloguj"
        logoutButton.setOnAction(event -> {
            System.out.println("Wylogowanie.");
            ViewUtils.switchView((Stage) logoutButton.getScene().getWindow(), "start-view.fxml");
        });
    }
}
