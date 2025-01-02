package com.example.voteapp.controllers;

import com.example.voteapp.model.Poll;
import com.example.voteapp.model.PollService;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Map;

public class PollResultsController {

    @FXML
    private BarChart<String, Number> resultsBarChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Button backButton;

    private final PollService pollService = new PollService();
    private Integer pollId;
    private Poll poll;

    @FXML
    private void initialize() {
        // Ustawienia osi X
        if (xAxis != null) {
            xAxis.setLabel("Opcje");
        }

        // Ustawienia osi Y
        if (yAxis != null) {
            yAxis.setLabel("Głosy");
            yAxis.setAutoRanging(false); // Wyłącz automatyczne dopasowanie zakresu
            yAxis.setLowerBound(0);      // Dolna granica
            yAxis.setUpperBound(10);     // Górna granica (zmień na większą, jeśli głosów może być więcej)
            yAxis.setTickUnit(1);        // Jednostka skali
        }

        // Ustawienia wykresu
        if (resultsBarChart != null) {
            resultsBarChart.setCategoryGap(20); // Odstęp między kategoriami (słupkami)
            resultsBarChart.setBarGap(5);      // Odstęp między słupkami w tej samej kategorii
        }

        System.out.println("PollResultsController initialized.");
    }

    public void setPoll(Poll poll) {
        this.poll = poll;

        if (poll != null) {
            this.pollId = poll.getId();
            loadResults();
        } else {
            System.err.println("Poll object is null. Cannot load results.");
        }
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
        loadResults();
    }

    private void loadResults() {
        resultsBarChart.getData().clear();

        Map<String, Integer> pollResults = pollService.getPollResults(poll.getId());
        System.out.println("Wyniki ankiety: " + pollResults);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Wyniki Głosowania");

        for (Map.Entry<String, Integer> entry : pollResults.entrySet()) {
            System.out.println("Opcja: " + entry.getKey() + ", Głosy: " + entry.getValue());
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        resultsBarChart.getData().add(series);

        // Aktualizacja górnej granicy osi Y na podstawie wyników
        int maxVotes = pollResults.values().stream().mapToInt(Integer::intValue).max().orElse(10);
        yAxis.setUpperBound(Math.max(maxVotes + 1, 10)); // Dopasuj górną granicę, minimum 10
    }

    @FXML
    private void handleBackButton() {
        if (backButton != null) {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        }
    }
}
