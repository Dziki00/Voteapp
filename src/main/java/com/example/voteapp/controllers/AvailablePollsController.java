package com.example.voteapp.controllers;

import com.example.voteapp.model.Poll;
import com.example.voteapp.model.PollService;
import javafx.application.Platform;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
            VBox pollBox = createPollBox(poll);
            pollsContainer.getChildren().add(pollBox);
        }
    }

    private VBox createPollBox(Poll poll) {
        VBox pollBox = new VBox(10);
        pollBox.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #dcdcdc; -fx-border-radius: 5;");

        Label pollNameLabel = new Label("Nazwa: " + poll.getName());
        pollNameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label pollStatusLabel = new Label("Status: " + (poll.isActive() ? "Aktywna" : "Zawieszona"));
        pollStatusLabel.setStyle("-fx-font-size: 14px;");

        Label countdownLabel = new Label();
        countdownLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FF5722;");

        Button voteButton = new Button("Zagłosuj");
        voteButton.setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        voteButton.setDisable(true); // Domyślnie zablokowane, dopóki nie zweryfikujemy stanu ankiety
        voteButton.setOnAction(e -> openVotePopup(poll));

        // Aktualizuj licznik czasu i zarządzaj dostępnością przycisku
        updateCountdownLabel(poll, countdownLabel, voteButton);

        pollBox.getChildren().addAll(pollNameLabel, pollStatusLabel, countdownLabel, voteButton);
        return pollBox;
    }

    private void updateCountdownLabel(Poll poll, Label countdownLabel, Button voteButton) {
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    if (poll.getStartDate().isAfter(now)) {
                        // Czas do rozpoczęcia
                        Duration duration = Duration.between(now, poll.getStartDate());
                        countdownLabel.setText("Czas do rozpoczęcia: " + formatDuration(duration));
                        voteButton.setDisable(true);
                    } else if (poll.getEndDate().isAfter(now)) {
                        // Czas do zakończenia
                        Duration duration = Duration.between(now, poll.getEndDate());
                        countdownLabel.setText("Czas do zakończenia: " + formatDuration(duration));
                        voteButton.setDisable(!poll.isActive());
                    } else {
                        // Po zakończeniu ankiety
                        countdownLabel.setText("Ankieta zakończona");
                        voteButton.setDisable(true);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000); // Odświeżaj co sekundę
    }

    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        if (days > 0) {
            return String.format("%d dni, %02d godz, %02d min, %02d sek", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%02d godz, %02d min, %02d sek", hours, minutes, seconds);
        } else {
            return String.format("%02d min, %02d sek", minutes, seconds);
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
