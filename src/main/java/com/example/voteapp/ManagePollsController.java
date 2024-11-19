package com.example.voteapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class ManagePollsController {

    @FXML
    private VBox pollsContainer;

    @FXML
    private void initialize() {
        if (pollsContainer != null) {
            for (int i = 1; i <= pollsContainer.getChildren().size(); i++) {
                TitledPane pollPane = (TitledPane) pollsContainer.getChildren().get(i - 1);
                VBox pollOptions = (VBox) pollPane.getContent();

                Button startButton = (Button) pollOptions.getChildren().get(0);
                Button editButton = (Button) pollOptions.getChildren().get(1);
                Button pauseButton = (Button) pollOptions.getChildren().get(2);
                Button endButton = (Button) pollOptions.getChildren().get(3);
                Button resultsButton = (Button) pollOptions.getChildren().get(4);
                Button deleteButton = (Button) pollOptions.getChildren().get(5);

                startButton.setOnAction(event -> System.out.println("Rozpoczęto ankietę " + pollPane.getText()));
                editButton.setOnAction(event -> System.out.println("Edytowano ankietę " + pollPane.getText()));
                pauseButton.setOnAction(event -> System.out.println("Wstrzymano ankietę " + pollPane.getText()));
                endButton.setOnAction(event -> System.out.println("Zakończono ankietę " + pollPane.getText()));
                resultsButton.setOnAction(event -> System.out.println("Pokazano wyniki ankiety " + pollPane.getText()));
                deleteButton.setOnAction(event -> System.out.println("Usunięto dane ankiety " + pollPane.getText()));
            }
        } else {
            System.out.println("pollsContainer is null");
        }
    }

}
