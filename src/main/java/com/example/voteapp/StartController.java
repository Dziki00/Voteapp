package com.example.voteapp;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class StartController {

    @FXML
    private TextField peselField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button checkVotesButton;

    @FXML
    private ImageView logoImage;

    private boolean isValidUser(String pesel, String password) {
        return pesel.matches("\\d{11}") && password.equals("user123");
    }
    @FXML
    private void initialize() {
        // Ustaw logo z ramką
        logoImage.setImage(new Image(getClass().getResourceAsStream("/com/example/voteapp/logo.png")));

        // Obsługa przycisków
        registerButton.setOnAction(event -> {
            ViewUtils.switchView((Stage) registerButton.getScene().getWindow(), "register-view.fxml");
        });

        checkVotesButton.setOnAction(event -> {
            ViewUtils.switchView((Stage) checkVotesButton.getScene().getWindow(), "votes-view.fxml");
        });


        loginButton.setOnAction(event -> {
            String pesel = peselField.getText();
            String password = passwordField.getText();


            if ("admin".equals(pesel) && "admin123".equals(password)) {
                // Przekierowanie do widoku administratora
                ViewUtils.switchView((Stage) loginButton.getScene().getWindow(), "admin-view.fxml");
            } else if ("12345678901".equals(pesel) && "user123".equals(password)) {
                // Przekierowanie do widoku użytkownika
                ViewUtils.switchView((Stage) loginButton.getScene().getWindow(), "user-view.fxml");
            } else {
                System.out.println("Błędne dane logowania!");
            }
        });
    }
}
