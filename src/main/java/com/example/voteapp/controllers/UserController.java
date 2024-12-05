package com.example.voteapp.controllers;

import com.example.voteapp.model.Poll;
import com.example.voteapp.model.PollService;
import com.example.voteapp.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class UserController {

    @FXML
    private Button voteButton;

    @FXML
    private Button logoutButton;


    private final PollService pollService = new PollService();

    @FXML
    private void initialize() {

        // Obsługa przycisku "Wyloguj"
        logoutButton.setOnAction(event -> {
            System.out.println("Wylogowanie.");
            ViewUtils.switchView((Stage) logoutButton.getScene().getWindow(), "start-view.fxml");
        });


        voteButton.setOnAction(event -> openAvailablePollsPopup());
    }



    private void openAvailablePollsPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/voteapp/available-polls-view.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Dostępne Ankiety");
            popupStage.setScene(new Scene(root, 1200, 800)); // Ustaw rozmiar pop-upu
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Nie udało się otworzyć okna dostępnych ankiet.");
        }
    }
}
