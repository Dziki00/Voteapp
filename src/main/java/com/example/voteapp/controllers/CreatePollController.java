package com.example.voteapp.controllers;

import com.example.voteapp.model.PollService;
import com.example.voteapp.model.Question;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        questionBox.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #dcdcdc; -fx-border-radius: 5;");

        // Pole pytania
        TextField questionField = new TextField();
        questionField.setPromptText("Treść pytania #" + questionCount);
        questionField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

        // Pole opcji
        VBox optionsBox = new VBox(5);
        TextField option1 = new TextField();
        option1.setPromptText("Opcja 1");
        TextField option2 = new TextField();
        option2.setPromptText("Opcja 2");
        optionsBox.getChildren().addAll(option1, option2);

        // Dodawanie kolejnych opcji
        Button addOptionButton = new Button("Dodaj opcję");
        addOptionButton.setOnAction(e -> {
            TextField newOption = new TextField();
            newOption.setPromptText("Opcja " + (optionsBox.getChildren().size() + 1));
            newOption.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
            optionsBox.getChildren().add(newOption);
        });

        questionBox.getChildren().addAll(questionField, optionsBox, addOptionButton);
        questionsContainer.getChildren().add(questionBox);
    }

    private void savePoll() {
        try {
            // Pobierz dane ankiety
            String pollName = pollNameField.getText();
            LocalDate startDate = startDateField.getValue();
            String startTime = startTimeField.getText();
            LocalDate endDate = endDateField.getValue();
            String endTime = endTimeField.getText();
            String voivodeship = voivodeshipField.getText();
            String municipality = municipalityField.getText();

            // Walidacja danych
            if (pollName.isEmpty() || startDate == null || endDate == null || startTime.isEmpty() || endTime.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Błąd Walidacji", "Wszystkie pola wymagane (oprócz województwa i gminy) muszą być wypełnione.");
                return;
            }

            LocalTime startLocalTime;
            LocalTime endLocalTime;
            try {
                startLocalTime = LocalTime.parse(startTime, TIME_FORMATTER);
                endLocalTime = LocalTime.parse(endTime, TIME_FORMATTER);
            } catch (Exception e) {
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
            List<Question> questions = new ArrayList<>();
            for (var node : questionsContainer.getChildren()) {
                if (node instanceof VBox) {
                    VBox questionBox = (VBox) node;
                    TextField questionField = (TextField) questionBox.getChildren().get(0);
                    String questionText = questionField.getText();
                    if (questionText.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Błąd Walidacji", "Treść pytania nie może być pusta.");
                        return;
                    }

                    VBox optionsBox = (VBox) questionBox.getChildren().get(1);
                    List<String> options = new ArrayList<>();
                    for (var optionNode : optionsBox.getChildren()) {
                        if (optionNode instanceof TextField) {
                            String optionText = ((TextField) optionNode).getText();
                            if (!optionText.isEmpty()) {
                                options.add(optionText);
                            }
                        }
                    }

                    if (options.size() < 2) {
                        showAlert(Alert.AlertType.ERROR, "Błąd Walidacji", "Każde pytanie musi mieć przynajmniej 2 opcje.");
                        return;
                    }

                    questions.add(new Question(questionText, options));
                }
            }

            if (questions.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Błąd Walidacji", "Ankieta musi zawierać przynajmniej jedno pytanie.");
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) createPollButton.getScene().getWindow();
        stage.close();
    }
}
