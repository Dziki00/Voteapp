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
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
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
        configureChart();
        configureQuestionSelector();
        System.out.println("PollResultsController initialized.");
    }

    private void configureChart() {
        if (xAxis != null) {
            xAxis.setLabel("Opcje");
        }
        if (yAxis != null) {
            yAxis.setLabel("Głosy");
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(10); // Domyślny zakres można dostosować
            yAxis.setTickUnit(1);
        }
        resultsBarChart.setAnimated(false); // Wyłączenie animacji dla lepszej czytelności
    }

    private void configureQuestionSelector() {
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
        try {
            List<String> questions = pollService.getQuestionsForPoll(pollId);
            questionSelector.setItems(FXCollections.observableArrayList(questions));
        } catch (Exception e) {
            System.err.println("Error loading questions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadResultsForQuestion(String questionText) {
        try {
            resultsBarChart.getData().clear();
            xAxis.getCategories().clear();

            // Pobierz ID pytania na podstawie tekstu
            int questionId = pollService.getQuestionIdByText(pollId, questionText);

            // Pobierz wyniki dla wybranego pytania
            Map<String, Integer> pollResults = pollService.getResultsForQuestion(questionId);
            System.out.println("Wyniki dla pytania \"" + questionText + "\": " + pollResults);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Wyniki");

            // Dodaj dane do wykresu i ustaw etykiety nad słupkami
            for (Map.Entry<String, Integer> entry : pollResults.entrySet()) {
                System.out.println("Opcja: " + entry.getKey() + ", Głosy: " + entry.getValue());
                XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
                series.getData().add(data);

                xAxis.getCategories().add(entry.getKey()); // Dodaj kategorię do osi X

                // Dodaj etykiety nad słupkami
                data.nodeProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        Label label = new Label(entry.getValue().toString());
                        label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
                        StackPane bar = (StackPane) newValue;
                        bar.getChildren().add(label);
                        label.setTranslateY(-15); // Przesunięcie etykiety w górę
                    }
                });
            }

            resultsBarChart.getData().add(series);

            // Dynamicznie ustaw zakres osi Y na podstawie wyników
            adjustYAxis(pollResults);

        } catch (Exception e) {
            System.err.println("Error loading results for question: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void adjustYAxis(Map<String, Integer> pollResults) {
        int maxVotes = pollResults.values().stream().max(Integer::compareTo).orElse(10);
        yAxis.setUpperBound(Math.max(10, maxVotes + 2)); // Dostosuj zakres z zapasem
    }
}
