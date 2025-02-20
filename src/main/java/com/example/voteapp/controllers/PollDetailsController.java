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
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class PollDetailsController {

    @FXML
    private Button suspendButton;

    @FXML
    private Button resultsButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label pollNameLabel;

    @FXML
    private Label pollStatusLabel;

    @FXML
    private Label pollStartDateLabel;

    @FXML
    private Label pollEndDateLabel;

    @FXML
    private Label pollCountdownLabel;

    private Poll poll;
    private Runnable refreshCallback;
    private Timer countdownTimer;
    private final PollService pollService = new PollService();

    @FXML
    private void initialize() {
        suspendButton.setOnAction(event -> togglePollStatus());
        resultsButton.setOnAction(event -> showPollResults());
        editButton.setOnAction(event -> openEditPollWindow());
        deleteButton.setOnAction(event -> deletePoll());
    }

    public void setPoll(Poll poll, Runnable refreshCallback) {
        this.poll = poll;
        this.refreshCallback = refreshCallback;

        if (poll != null) {
            loadPollDetails();
            startCountdown();
        } else {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie można załadować szczegółów ankiety. Brak danych.");
        }
    }

    private void loadPollDetails() {
        try {
            pollNameLabel.setText("Nazwa: " + poll.getName());
            pollStatusLabel.setText("Status: " + (poll.isActive() ? "Aktywna" : "Zawieszona"));
            pollStartDateLabel.setText("Data rozpoczęcia: " + poll.getStartDate());
            pollEndDateLabel.setText("Data zakończenia: " + poll.getEndDate());
        } catch (NullPointerException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Brak wymaganych danych do wyświetlenia szczegółów ankiety.");
        }
    }

    private void startCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        countdownTimer = new Timer(true);
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateCountdown());
            }
        }, 0, 1000);
    }

    private void updateCountdown() {
        LocalDateTime now = LocalDateTime.now();

        if (poll.getStartDate().isAfter(now)) {
            Duration timeToStart = Duration.between(now, poll.getStartDate());
            pollCountdownLabel.setText(formatDuration(timeToStart, "Do rozpoczęcia: "));
        } else if (poll.getEndDate().isAfter(now)) {
            Duration timeToEnd = Duration.between(now, poll.getEndDate());
            pollCountdownLabel.setText(formatDuration(timeToEnd, "Do zakończenia: "));
        } else {
            pollCountdownLabel.setText("Ankieta zakończona.");
            pollStatusLabel.setText("Status: Zakończona");
            poll.setActive(false);
            disableActionsAfterEnd();
            countdownTimer.cancel();
        }
    }

    private void disableActionsAfterEnd() {
        suspendButton.setDisable(true);
        editButton.setDisable(true);
        deleteButton.setDisable(false); // Usunięcie ankiety zawsze możliwe
        resultsButton.setDisable(false); // Wyniki zawsze aktywne
    }

    private String formatDuration(Duration duration, String prefix) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        return prefix + days + "d " + hours + "g " + minutes + "m " + seconds + "s";
    }

    private void togglePollStatus() {
        try {
            boolean success = pollService.togglePollStatus(poll);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Status ankiety został zmieniony.");
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
                pollStatusLabel.setText("Status: " + (poll.isActive() ? "Aktywna" : "Zawieszona"));
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zmienić statusu ankiety.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd podczas zmiany statusu ankiety: " + e.getMessage());
        }
    }

    private void deletePoll() {
        try {
            boolean success = pollService.deletePoll(poll);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Ankieta została usunięta.");
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się usunąć ankiety.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd podczas usuwania ankiety: " + e.getMessage());
        }
    }

    private void showPollResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/voteapp/poll-results-view.fxml"));
            Parent root = loader.load();

            PollResultsController controller = loader.getController();
            controller.setPoll(poll); // Przekazanie obiektu Poll

            Stage stage = new Stage();
            stage.setTitle("Wyniki Ankiety");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć widoku wyników.");
        }
    }



    private void openEditPollWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/voteapp/edit-poll-view.fxml"));
            Parent root = fxmlLoader.load();

            EditPollController editPollController = fxmlLoader.getController();
            editPollController.setPoll(poll, this::refreshPollDetails);

            Stage stage = new Stage();
            stage.setTitle("Edycja Ankiety");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć okna edycji ankiety.");
        }
    }

    @FXML
    private void closeWindow() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
        if (suspendButton != null && suspendButton.getScene() != null) {
            Stage stage = (Stage) suspendButton.getScene().getWindow();
            stage.close();
        }
    }

    private void refreshPollDetails() {
        if (poll != null) {
            loadPollDetails();
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
