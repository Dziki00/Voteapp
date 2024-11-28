package com.example.voteapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class ManagePollsController {

    @FXML
    private VBox pollsContainer;

    @FXML
    private void initialize() {
        if (pollsContainer == null) {
            System.out.println("pollsContainer is null. Please check your FXML file.");
            return;
        }

        // Iterate through all child elements (TitledPanes) in the pollsContainer VBox
        for (int i = 0; i < pollsContainer.getChildren().size(); i++) {
            if (pollsContainer.getChildren().get(i) instanceof TitledPane) {
                TitledPane pollPane = (TitledPane) pollsContainer.getChildren().get(i);

                if (pollPane.getContent() instanceof VBox) {
                    VBox pollOptions = (VBox) pollPane.getContent();

                    // Ensure that all expected buttons are present
                    if (pollOptions.getChildren().size() >= 6) {
                        Button startButton = (Button) pollOptions.getChildren().get(0);
                        Button editButton = (Button) pollOptions.getChildren().get(1);
                        Button pauseButton = (Button) pollOptions.getChildren().get(2);
                        Button endButton = (Button) pollOptions.getChildren().get(3);
                        Button resultsButton = (Button) pollOptions.getChildren().get(4);
                        Button deleteButton = (Button) pollOptions.getChildren().get(5);

                        // Assign event handlers to each button
                        assignButtonActions(pollPane, startButton, editButton, pauseButton, endButton, resultsButton, deleteButton);
                    } else {
                        System.out.println("Poll options for " + pollPane.getText() + " are incomplete.");
                    }
                } else {
                    System.out.println("Invalid content in pollPane: " + pollPane.getText());
                }
            } else {
                System.out.println("Non-TitledPane child detected in pollsContainer.");
            }
        }
    }

    private void assignButtonActions(TitledPane pollPane, Button startButton, Button editButton, Button pauseButton,
                                     Button endButton, Button resultsButton, Button deleteButton) {
        String pollTitle = pollPane.getText();

        startButton.setOnAction(event -> System.out.println("Rozpoczęto ankietę: " + pollTitle));
        editButton.setOnAction(event -> System.out.println("Edytowano ankietę: " + pollTitle));
        pauseButton.setOnAction(event -> System.out.println("Wstrzymano ankietę: " + pollTitle));
        endButton.setOnAction(event -> System.out.println("Zakończono ankietę: " + pollTitle));
        resultsButton.setOnAction(event -> System.out.println("Pokazano wyniki ankiety: " + pollTitle));
        deleteButton.setOnAction(event -> System.out.println("Usunięto dane ankiety: " + pollTitle));
    }
}
