package com.example.voteapp.controllers;

import com.example.voteapp.model.PollService;
import com.example.voteapp.model.Question;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CreatePollController {

    @FXML
    private TextField pollNameField;

    @FXML
    private DatePicker startDateField;

    @FXML
    private TextField startTimeField;

    @FXML
    private DatePicker endDateField;

    @FXML
    private TextField endTimeField;

    @FXML
    private TextField voivodeshipField;

    @FXML
    private TextField municipalityField;

    @FXML
    private VBox questionsContainer;

    @FXML
    private Button addQuestionButton;

    @FXML
    private Button createPollButton;

    private int questionCount = 0;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private void initialize() {
        addQuestionButton.setOnAction(event -> addQuestion());
        createPollButton.setOnAction(event -> savePoll());
    }

    private void addQuestion() {
        questionCount++;

        VBox questionBox = new VBox(10);
        questionBox.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 5;");

        // Pole pytania
        TextField questionField = new TextField();
        questionField.setPromptText("Treść pytania #" + questionCount);
        questionField.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #E3F2FD; -fx-border-color: #64B5F6; -fx-border-radius: 5;");

        // Kontener na opcje
        VBox optionsBox = new VBox(5);
        addOption(optionsBox);
        addOption(optionsBox);

        // Dodawanie opcji
        Button addOptionButton = new Button("Dodaj opcję");
        addOptionButton.setStyle("-fx-background-color: #43A047; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        addOptionButton.setOnAction(e -> addOption(optionsBox));

        // Usuwanie pytania
        Button deleteQuestionButton = new Button("Usuń pytanie");
        deleteQuestionButton.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        deleteQuestionButton.setOnAction(e -> {
            questionsContainer.getChildren().remove(questionBox);
            questionCount--;
        });

        questionBox.getChildren().addAll(questionField, optionsBox, addOptionButton, deleteQuestionButton);
        questionsContainer.getChildren().add(questionBox);
    }

    private void addOption(VBox optionsBox) {
        HBox optionBox = new HBox(10);
        optionBox.setStyle("-fx-padding: 5; -fx-border-color: #dcdcdc; -fx-background-color: #ffffff; -fx-border-radius: 5;");

        TextField optionField = new TextField();
        optionField.setPromptText("Treść opcji");
        optionField.setStyle("-fx-font-size: 14px; -fx-padding: 5; -fx-background-color: #E8F5E9; -fx-border-color: #A5D6A7; -fx-border-radius: 5;");

        Button deleteOptionButton = new Button("Usuń opcję");
        deleteOptionButton.setStyle("-fx-background-color: #E53935; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        deleteOptionButton.setOnAction(e -> optionsBox.getChildren().remove(optionBox));

        optionBox.getChildren().addAll(optionField, deleteOptionButton);
        optionsBox.getChildren().add(optionBox);
    }

    private void savePoll() {
        try {
            // Walidacja danych podstawowych
            String pollName = pollNameField.getText();
            LocalDate startDate = startDateField.getValue();
            String startTime = startTimeField.getText();
            LocalDate endDate = endDateField.getValue();
            String endTime = endTimeField.getText();
            String voivodeship = voivodeshipField.getText();
            String municipality = municipalityField.getText();

            if (pollName.isEmpty() || startDate == null || endDate == null || startTime.isEmpty() || endTime.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Błąd Walidacji", "Wszystkie wymagane pola muszą być wypełnione.");
                return;
            }

            LocalTime startLocalTime = parseTime(startTime);
            LocalTime endLocalTime = parseTime(endTime);

            if (startLocalTime == null || endLocalTime == null) {
                showAlert(Alert.AlertType.ERROR, "Błąd Formatu", "Podaj czas w formacie HH:mm.");
                return;
            }

            LocalDateTime startDateTime = LocalDateTime.of(startDate, startLocalTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endLocalTime);

            if (endDateTime.isBefore(startDateTime)) {
                showAlert(Alert.AlertType.ERROR, "Błąd Walidacji", "Data zakończenia nie może być wcześniejsza niż data rozpoczęcia.");
                return;
            }

            // Pobierz pytania i opcje
            List<Question> questions = getQuestionsAndOptions();
            if (questions.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Błąd Walidacji", "Ankieta musi zawierać przynajmniej jedno pytanie z opcjami.");
                return;
            }

            // Zapis ankiety do bazy danych
            PollService pollService = new PollService();
            boolean success = pollService.savePoll(pollName, startDateTime, endDateTime, voivodeship, municipality, questions);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Ankieta została zapisana pomyślnie.");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zapisać ankiety.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd podczas zapisywania ankiety.");
            e.printStackTrace();
        }
    }

    private LocalTime parseTime(String time) {
        try {
            return LocalTime.parse(time, TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private List<Question> getQuestionsAndOptions() {
        List<Question> questions = new ArrayList<>();

        for (var node : questionsContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox questionBox = (VBox) node;
                TextField questionField = (TextField) questionBox.getChildren().get(0);
                String questionText = questionField.getText();

                if (questionText.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Błąd Walidacji", "Treść pytania nie może być pusta.");
                    return new ArrayList<>();
                }

                VBox optionsBox = (VBox) questionBox.getChildren().get(1);
                List<String> options = new ArrayList<>();

                for (var optionNode : optionsBox.getChildren()) {
                    if (optionNode instanceof HBox) {
                        HBox optionBox = (HBox) optionNode;
                        TextField optionField = (TextField) optionBox.getChildren().get(0);
                        String optionText = optionField.getText();

                        if (!optionText.isEmpty()) {
                            options.add(optionText);
                        }
                    }
                }

                if (options.size() < 2) {
                    showAlert(Alert.AlertType.ERROR, "Błąd Walidacji", "Każde pytanie musi mieć przynajmniej 2 opcje.");
                    return new ArrayList<>();
                }

                questions.add(new Question(questionText, options));
            }
        }
        return questions;
    }

    private void clearForm() {
        pollNameField.clear();
        startDateField.setValue(null);
        startTimeField.clear();
        endDateField.setValue(null);
        endTimeField.clear();
        voivodeshipField.clear();
        municipalityField.clear();
        questionsContainer.getChildren().clear();
        questionCount = 0;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) createPollButton.getScene().getWindow();
        stage.close();
    }
}
