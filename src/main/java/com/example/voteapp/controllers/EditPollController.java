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
    private Runnable onEditCallback; // Funkcja wywoływana po edycji
    private final PollService pollService = new PollService();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setPoll(Poll poll, Runnable onEditCallback) {
        this.poll = poll;
        this.onEditCallback = onEditCallback;

        if (poll != null) {
            pollNameField.setText(poll.getName());
            startDateField.setText(poll.getStartDate().format(DATE_TIME_FORMATTER));
            endDateField.setText(poll.getEndDate().format(DATE_TIME_FORMATTER));
            voivodeshipField.setText(poll.getVoivodeship());
            municipalityField.setText(poll.getMunicipality());
        } else {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie można załadować danych ankiety.");
        }
    }

    @FXML
    private void saveChanges() {
        try {
            // Sprawdzenie, czy pola tekstowe nie są puste ani null
            if (pollNameField == null || pollNameField.getText() == null || pollNameField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Nazwa ankiety nie może być pusta.");
            }
            if (startDateField == null || startDateField.getText() == null || startDateField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Data rozpoczęcia jest wymagana.");
            }
            if (endDateField == null || endDateField.getText() == null || endDateField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Data zakończenia jest wymagana.");
            }
            if (voivodeshipField == null || voivodeshipField.getText() == null) {
                voivodeshipField.setText(""); // Domyślnie puste województwo
            }
            if (municipalityField == null || municipalityField.getText() == null) {
                municipalityField.setText(""); // Domyślnie pusta gmina
            }

            // Pobierz dane z pól
            poll.setName(pollNameField.getText().trim());
            poll.setStartDate(LocalDateTime.parse(startDateField.getText().trim(), DATE_TIME_FORMATTER));
            poll.setEndDate(LocalDateTime.parse(endDateField.getText().trim(), DATE_TIME_FORMATTER));
            poll.setVoivodeship(voivodeshipField.getText().trim());
            poll.setMunicipality(municipalityField.getText().trim());

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
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowy format daty. Użyj formatu: yyyy-MM-dd HH:mm.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", e.getMessage());
        }
    }

    private void validateFields() {
        if (pollNameField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwa ankiety nie może być pusta.");
        }
        if (startDateField.getText().trim().isEmpty() || endDateField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Daty rozpoczęcia i zakończenia są wymagane.");
        }
        if (LocalDateTime.parse(startDateField.getText(), DATE_TIME_FORMATTER)
                .isAfter(LocalDateTime.parse(endDateField.getText(), DATE_TIME_FORMATTER))) {
            throw new IllegalArgumentException("Data rozpoczęcia nie może być późniejsza niż data zakończenia.");
        }
        if (voivodeshipField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Województwo nie może być puste.");
        }
        if (municipalityField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Gmina nie może być pusta.");
        }
    }

    @FXML
    private void closeWindow() {
        if (pollNameField.getScene() != null) {
            pollNameField.getScene().getWindow().hide();
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
