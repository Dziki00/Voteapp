package com.example.voteapp.controllers;

import com.example.voteapp.model.UserService;
import com.example.voteapp.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController {

    private final UserService userService = new UserService();

    @FXML
    private TextField peselField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private ImageView logoImage;

    @FXML
    private void initialize() {
        // Ustaw logo z ramką
        logoImage.setImage(new Image(getClass().getResourceAsStream("/com/example/voteapp/logo.png")));

        // Obsługa przycisku rejestracji
        registerButton.setOnAction(event -> {
            ViewUtils.switchView((Stage) registerButton.getScene().getWindow(), "register-view.fxml");
        });

        // Obsługa przycisku logowania
        loginButton.setOnAction(event -> {
            String pesel = peselField.getText();
            String password = passwordField.getText();

            // Obsługa logowania dla administratora
            if ("admin".equals(pesel) && "admin123".equals(password)) {
                System.out.println("Logowanie administratora.");
                ViewUtils.switchView((Stage) loginButton.getScene().getWindow(), "admin-view.fxml");
                return;
            }

            // Obsługa logowania dla użytkownika
            boolean success = userService.loginUser(pesel, password);
            if (success) {
                System.out.println("Logowanie użytkownika zakończone sukcesem.");
                ViewUtils.switchView((Stage) loginButton.getScene().getWindow(), "user-view.fxml");
            } else {
                System.out.println("Błędne dane logowania!");
            }
        });
    }
}
