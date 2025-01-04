package com.example.voteapp.controllers;

import com.example.voteapp.model.Poll;
import com.example.voteapp.model.PollService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
    private int userId; // Pole do przechowywania ID użytkownika

    // Metoda do ustawiania userId
    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("Ustawiono userId w AvailablePollsController: " + userId);
        loadAvailablePolls();
    }

    @FXML
    private void initialize() {
        System.out.println("AvailablePollsController został zainicjalizowany.");
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
        voteButton.setDisable(true);
        voteButton.setOnAction(e -> openVotePopup(poll));

        Button resultsButton = new Button("Wyniki");
        resultsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        resultsButton.setDisable(true);
        resultsButton.setOnAction(e -> openResultsPopup(poll));

        updateCountdownLabel(poll, countdownLabel, voteButton, resultsButton);

        pollBox.getChildren().addAll(pollNameLabel, pollStatusLabel, countdownLabel, voteButton, resultsButton);
        return pollBox;
    }

    private void updateCountdownLabel(Poll poll, Label countdownLabel, Button voteButton, Button resultsButton) {
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    if (poll.getStartDate().isAfter(now)) {
                        Duration duration = Duration.between(now, poll.getStartDate());
                        countdownLabel.setText("Czas do rozpoczęcia: " + formatDuration(duration));
                        voteButton.setDisable(true);
                        resultsButton.setDisable(true);
                    } else if (poll.getEndDate().isAfter(now)) {
                        Duration duration = Duration.between(now, poll.getEndDate());
                        countdownLabel.setText("Czas do zakończenia: " + formatDuration(duration));
                        voteButton.setDisable(!poll.isActive());
                        resultsButton.setDisable(true);
                    } else {
                        countdownLabel.setText("Ankieta zakończona");
                        voteButton.setDisable(true);
                        resultsButton.setDisable(false);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
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

            VoteController voteController = loader.getController();
            voteController.setPoll(poll, userId);

            Stage voteStage = new Stage();
            voteStage.initModality(Modality.APPLICATION_MODAL);
            voteStage.setTitle("Głosowanie");
            voteStage.setScene(new Scene(root, 600, 400));
            voteStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć okna głosowania.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił niespodziewany błąd.");
        }
    }


    private void openResultsPopup(Poll poll) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/voteapp/poll-results-view.fxml"));
            Parent root = loader.load();

            PollResultsController resultsController = loader.getController();
            resultsController.setPoll(poll);

            Stage resultsStage = new Stage();
            resultsStage.initModality(Modality.APPLICATION_MODAL);
            resultsStage.setTitle("Wyniki ankiety: " + poll.getName());
            resultsStage.setScene(new Scene(root, 800, 600));
            resultsStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć okna wyników.");
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
