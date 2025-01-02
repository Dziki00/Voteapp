package com.example.voteapp.controllers;

import com.example.voteapp.model.Poll;
import com.example.voteapp.model.PollService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class PollResultsController {

    @FXML
    private BarChart<String, Number> resultsBarChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private ComboBox<String> questionSelector;

    @FXML
    private Button backButton;

    private final PollService pollService = new PollService();
    private Integer pollId;
    private Poll poll;

    @FXML
    private void initialize() {
        if (xAxis != null) {
            xAxis.setLabel("Opcje");
        }
        if (yAxis != null) {
            yAxis.setLabel("Głosy");
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(10);
            yAxis.setTickUnit(1);
        }
        System.out.println("PollResultsController initialized.");

        // Obsługa zmiany wybranego pytania
        questionSelector.setOnAction(event -> {
            String selectedQuestion = questionSelector.getValue();
            if (selectedQuestion != null) {
                loadResultsForQuestion(selectedQuestion);
            }
        });
    }

    public void setPoll(Poll poll) {
        this.poll = poll;

        if (poll != null) {
            this.pollId = poll.getId();
            loadQuestions();
        } else {
            System.err.println("Poll object is null. Cannot load results.");
        }
    }

    private void loadQuestions() {
        List<String> questions = pollService.getQuestionsForPoll(pollId);
        questionSelector.setItems(FXCollections.observableArrayList(questions));
    }

    private void loadResultsForQuestion(String questionText) {
        resultsBarChart.getData().clear();
        xAxis.getCategories().clear(); // Upewnij się, że czyścisz poprzednie kategorie

        // Pobierz ID pytania na podstawie tekstu
        int questionId = pollService.getQuestionIdByText(pollId, questionText);

        // Pobierz wyniki dla wybranego pytania
        Map<String, Integer> pollResults = pollService.getResultsForQuestion(questionId);
        System.out.println("Wyniki dla pytania \"" + questionText + "\": " + pollResults);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Wyniki Głosowania");

        // Dodaj dane do wykresu
        for (Map.Entry<String, Integer> entry : pollResults.entrySet()) {
            System.out.println("Opcja: " + entry.getKey() + ", Głosy: " + entry.getValue());
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            xAxis.getCategories().add(entry.getKey()); // Dodaj kategorię do osi X
        }

        resultsBarChart.getData().add(series);
    }


    @FXML
    private void handleBackButton() {
        if (backButton != null) {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        }
    }
}
