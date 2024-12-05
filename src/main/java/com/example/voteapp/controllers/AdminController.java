package com.example.voteapp.controllers;

import com.example.voteapp.model.Poll;
import com.example.voteapp.model.PollService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AdminController {

    @FXML
    private Button createPollButton;

    @FXML
    private Button logoutButton;

    @FXML
    private VBox pollsContainer;

    private final PollService pollService = new PollService();

    @FXML
    private void initialize() {
        // Obsługa przycisku "Utwórz Ankietę"
        createPollButton.setOnAction(event -> openCreatePollPopup());

        // Obsługa przycisku "Wyloguj"
        logoutButton.setOnAction(event -> logout());

        // Załaduj ankiety przy inicjalizacji
        loadPolls();
    }

    private void openCreatePollPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/voteapp/create-poll-view.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Utwórz Nową Ankietę");
            popupStage.setScene(new Scene(root, 1000, 800)); // Ustaw rozmiar pop-upu
            popupStage.setOnHiding(event -> loadPolls()); // Odśwież listę po zamknięciu okna
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć okna tworzenia ankiety.");
        }
    }

    private void loadPolls() {
        pollsContainer.getChildren().clear();

        List<Poll> polls = pollService.getAllPolls();
        if (polls.isEmpty()) {
            Label noPollsLabel = new Label("Brak dostępnych ankiet.");
            noPollsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888888;");
            pollsContainer.getChildren().add(noPollsLabel);
            return;
        }

        for (Poll poll : polls) {
            VBox pollBox = new VBox(10);
            pollBox.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #dcdcdc; -fx-border-radius: 5;");

            Label pollInfo = new Label("Nazwa: " + poll.getName() +
                    "\nStatus: " + (poll.isActive() ? "Aktywna" : "Zawieszona") +
                    "\nData rozpoczęcia: " + poll.getStartDate() +
                    "\nData zakończenia: " + poll.getEndDate());
            pollInfo.setStyle("-fx-font-size: 14px;");

            // Przycisk Szczegóły
            Button detailsButton = new Button("Szczegóły");
            detailsButton.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
            detailsButton.setOnAction(e -> openPollDetailsPopup(poll));

            // Przycisk Usuń
            Button deleteButton = new Button("Usuń");
            deleteButton.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
            deleteButton.setOnAction(e -> deletePoll(poll));

            pollBox.getChildren().addAll(pollInfo, detailsButton, deleteButton);
            pollsContainer.getChildren().add(pollBox);
        }
    }

    private void openPollDetailsPopup(Poll poll) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/voteapp/poll-details-view.fxml"));
            Parent root = loader.load();

            PollDetailsController controller = loader.getController();
            controller.setPoll(poll, this::loadPolls); // Przekazujemy ankietę i funkcję odświeżania listy

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Szczegóły Ankiety");
            popupStage.setScene(new Scene(root, 600, 400));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć szczegółów ankiety.");
        }
    }


    private void deletePoll(Poll poll) {
        boolean success = pollService.deletePoll(poll);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Ankieta została pomyślnie usunięta.");
            loadPolls();
        } else {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się usunąć ankiety.");
        }
    }

    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/voteapp/start-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Logowanie");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się przełączyć na ekran logowania.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
