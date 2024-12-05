package com.example.voteapp.controllers;

import com.example.voteapp.model.Poll;
import com.example.voteapp.model.PollService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EditPollController {

    @FXML
    private TextField pollNameField;

    @FXML
    private TextField startDateField;

    @FXML
    private TextField endDateField;

    @FXML
    private TextField voivodeshipField;

    @FXML
    private TextField municipalityField;

    private Poll poll;
    private Runnable onEditCallback;
    private final PollService pollService = new PollService();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setPoll(Poll poll, Runnable onEditCallback) {
        this.poll = poll;
        this.onEditCallback = onEditCallback;

        // Wypełnij pola danymi ankiety
        pollNameField.setText(poll.getName());
        startDateField.setText(poll.getStartDate().format(DATE_TIME_FORMATTER));
        endDateField.setText(poll.getEndDate().format(DATE_TIME_FORMATTER));
        voivodeshipField.setText(poll.getVoivodeship());
        municipalityField.setText(poll.getMunicipality());
    }

    @FXML
    private void saveChanges() {
        try {
            // Pobierz zaktualizowane dane z pól
            poll.setName(pollNameField.getText());
            poll.setStartDate(LocalDateTime.parse(startDateField.getText(), DATE_TIME_FORMATTER));
            poll.setEndDate(LocalDateTime.parse(endDateField.getText(), DATE_TIME_FORMATTER));
            poll.setVoivodeship(voivodeshipField.getText());
            poll.setMunicipality(municipalityField.getText());

            // Aktualizuj ankietę
            boolean success = pollService.updatePoll(poll);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Dane ankiety zostały zaktualizowane.");
                closeWindow();
                if (onEditCallback != null) onEditCallback.run();
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zapisać zmian.");
            }
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowy format daty.");
        }
    }

    @FXML
    private void closeWindow() {
        pollNameField.getScene().getWindow().hide();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
