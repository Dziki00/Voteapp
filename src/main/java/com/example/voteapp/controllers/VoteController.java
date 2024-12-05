package com.example.voteapp.controllers;

import com.example.voteapp.model.Poll;
import com.example.voteapp.model.PollService;
import com.example.voteapp.model.Question;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteController {

    @FXML
    private Label pollNameLabel;

    @FXML
    private VBox questionsContainer;

    @FXML
    private Button submitVoteButton;

    private Poll poll;
    private final Map<Question, String> selectedOptions = new HashMap<>();
    private final PollService pollService = new PollService();
    private int userId;

    public void setPoll(Poll poll, int userId) {
        this.poll = poll;
        this.userId = userId;
        pollNameLabel.setText("Głosowanie: " + poll.getName());
        checkIfUserCanVote();
    }


    private void checkIfUserCanVote() {
        boolean hasVoted = pollService.hasUserVoted(poll.getId(), userId);

        if (hasVoted) {
            disableVoting("Już zagłosowałeś w tej ankiecie.");
        } else if (!poll.isActive()) {
            disableVoting("Ankieta nie jest aktywna.");
        } else {
            loadQuestions();
        }
    }

    private void disableVoting(String message) {
        submitVoteButton.setDisable(true);
        questionsContainer.getChildren().clear();

        Label infoLabel = new Label(message);
        infoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");
        questionsContainer.getChildren().add(infoLabel);
    }

    private void loadQuestions() {
        questionsContainer.getChildren().clear();
        List<Question> questions = poll.getQuestions();

        for (Question question : questions) {
            VBox questionBox = new VBox(10);
            questionBox.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-border-color: #dcdcdc; -fx-border-radius: 5;");

            Label questionLabel = new Label(question.getQuestionText());
            questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            ToggleGroup optionsGroup = new ToggleGroup();
            VBox optionsBox = new VBox(5);
            for (String option : question.getOptions()) {
                RadioButton optionButton = new RadioButton(option);
                optionButton.setStyle("-fx-font-size: 14px;");
                optionButton.setToggleGroup(optionsGroup);

                optionButton.setOnAction(event -> selectedOptions.put(question, option));

                optionsBox.getChildren().add(optionButton);
            }

            questionBox.getChildren().addAll(questionLabel, optionsBox);
            questionsContainer.getChildren().add(questionBox);
        }
    }

    @FXML
    private void submitVote() {
        if (validateSelections()) {
            boolean allVotesSaved = saveVoteToDatabase();
            if (allVotesSaved) {
                disableVoting("Twój głos został zapisany.");
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Twój głos został zapisany.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd podczas zapisywania odpowiedzi.");
            }
        }
    }

    private boolean validateSelections() {
        for (Question question : poll.getQuestions()) {
            if (!selectedOptions.containsKey(question)) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Musisz wybrać odpowiedź dla każdego pytania.");
                return false;
            }
        }
        return true;
    }

    private boolean saveVoteToDatabase() {
        boolean allVotesSaved = true;

        for (Map.Entry<Question, String> entry : selectedOptions.entrySet()) {
            Question question = entry.getKey();
            String selectedOption = entry.getValue();

            boolean success = pollService.saveUserVote(poll.getId(), userId, question.getId(), selectedOption);

            if (!success) {
                allVotesSaved = false;
                showAlert(Alert.AlertType.ERROR, "Błąd", "Wystąpił błąd podczas zapisu głosu dla pytania: " + question.getQuestionText());
            }
        }

        return allVotesSaved;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
