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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

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

    private Poll poll;
    private Runnable refreshCallback;

    private final PollService pollService = new PollService();

    @FXML
    private void initialize() {
        // Ustawienie akcji dla przycisków
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
        } else {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie można załadować szczegółów ankiety. Brak danych.");
        }
    }

    private void loadPollDetails() {
        try {
            // Załaduj szczegóły ankiety do widoku
            pollNameLabel.setText("Nazwa: " + poll.getName());
            pollStatusLabel.setText("Status: " + (poll.isActive() ? "Aktywna" : "Zawieszona"));
            pollStartDateLabel.setText( poll.getStartDate().toString());
            pollEndDateLabel.setText(poll.getEndDate().toString());
        } catch (NullPointerException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Brak wymaganych danych do wyświetlenia szczegółów ankiety.");
        }
    }

    private void togglePollStatus() {
        try {
            boolean success = pollService.togglePollStatus(poll);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Status ankiety został zmieniony.");
                if (refreshCallback != null) {
                    refreshCallback.run(); // Odświeżanie listy ankiet
                }
                // Zaktualizuj status w widoku
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
                    refreshCallback.run(); // Odświeżanie listy ankiet
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
        // Funkcjonalność wyświetlania wyników ankiety (opcjonalnie do implementacji)
        showAlert(Alert.AlertType.INFORMATION, "Wyniki ankiety", "Wyświetlanie wyników ankiety w trakcie implementacji.");
    }

    private void openEditPollWindow() {
        try {
            // Załaduj plik FXML dla edycji ankiety
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/voteapp/edit-poll-view.fxml"));
            Parent root = loader.load();

            // Pobierz kontroler edycji ankiety
            EditPollController controller = loader.getController();

            // Przekaż dane ankiety i funkcję odświeżania
            controller.setPoll(poll, refreshCallback);

            // Utwórz i skonfiguruj nowe okno
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Okno modalne
            popupStage.setTitle("Edycja Ankiety");
            popupStage.setScene(new Scene(root, 600, 400)); // Ustaw rozmiar okna
            popupStage.showAndWait();

            // Odśwież dane po zamknięciu okna
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć okna edycji ankiety.");
        }
    }

    @FXML
    private void closeWindow() {
        // Zamknięcie okna szczegółów ankiety
        if (suspendButton != null && suspendButton.getScene() != null) {
            Stage stage = (Stage) suspendButton.getScene().getWindow();
            stage.close();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        // Wyświetlanie powiadomień
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
