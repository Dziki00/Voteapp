package com.example.voteapp;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class VotesController {

    @FXML
    private BarChart<String, Number> survey1BarChart;

    @FXML
    private BarChart<String, Number> survey2BarChart;

    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        // Ankieta 1: Wyniki "Ulubiony owoc"
        XYChart.Series<String, Number> fruitData = new XYChart.Series<>();
        fruitData.setName("Głosy");
        fruitData.getData().add(new XYChart.Data<>("Jabłko", 50));
        fruitData.getData().add(new XYChart.Data<>("Banan", 30));
        fruitData.getData().add(new XYChart.Data<>("Truskawka", 20));
        survey1BarChart.getData().add(fruitData);

        // Ankieta 2: Wyniki "Ulubiona pora roku"
        XYChart.Series<String, Number> seasonData = new XYChart.Series<>();
        seasonData.setName("Głosy");
        seasonData.getData().add(new XYChart.Data<>("Wiosna", 40));
        seasonData.getData().add(new XYChart.Data<>("Lato", 60));
        seasonData.getData().add(new XYChart.Data<>("Jesień", 25));
        seasonData.getData().add(new XYChart.Data<>("Zima", 15));
        survey2BarChart.getData().add(seasonData);

        // Obsługa przycisku "Powrót"
        backButton.setOnAction(event -> {
            ViewUtils.switchView((Stage) backButton.getScene().getWindow(), "start-view.fxml");
        });
    }
}
