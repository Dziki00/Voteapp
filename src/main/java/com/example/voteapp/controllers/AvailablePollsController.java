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

public class AvailablePollsController {

    @FXML
    private VBox pollsContainer;

    private final PollService pollService = new PollService();

    @FXML
    private void initialize() {
        loadAvailablePolls();
    }

    private void loadAvailablePolls() {
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

            Button voteButton = new Button("Zagłosuj");
            voteButton.setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
            voteButton.setOnAction(e -> openVotePopup(poll));

            pollBox.getChildren().addAll(pollInfo, voteButton);
            pollsContainer.getChildren().add(pollBox);
        }
    }

    private void openVotePopup(Poll poll) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/voteapp/vote-view.fxml"));
            Parent root = loader.load();

            VoteController controller = loader.getController();

            // Pobieranie identyfikatora użytkownika
            int userId = getCurrentUserId();
            controller.setPoll(poll, userId);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Głosowanie");
            popupStage.setScene(new Scene(root, 400, 300));
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć okna głosowania.");
        }
    }

    private int getCurrentUserId() {
        // Implementacja pobierania bieżącego identyfikatora użytkownika (np. z sesji lub innej klasy).
        return 1; // Placeholder. Zamień na właściwe pobieranie userId.
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
