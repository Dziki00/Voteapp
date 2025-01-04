package com.example.voteapp.controllers;

import com.example.voteapp.model.UserService;
import com.example.voteapp.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
        // Ustaw logo
        logoImage.setImage(new Image(getClass().getResourceAsStream("/com/example/voteapp/logo.png")));

        // Obsługa przycisku rejestracji
        registerButton.setOnAction(event -> {
            ViewUtils.switchView((Stage) registerButton.getScene().getWindow(), "register-view.fxml");
        });

        // Obsługa przycisku logowania
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String pesel = peselField.getText().trim();
        String password = passwordField.getText().trim();

        if (pesel.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Błąd logowania", "PESEL i hasło nie mogą być puste.");
            return;
        }

        // Logowanie administratora
        if ("admin".equals(pesel) && "admin123".equals(password)) {
            System.out.println("Logowanie administratora.");
            ViewUtils.switchView((Stage) loginButton.getScene().getWindow(), "admin-view.fxml");
            return;
        }

        // Logowanie użytkownika
        Integer userId = userService.getUserId(pesel, password);
        if (userId != null) {
            System.out.println("Logowanie użytkownika zakończone sukcesem.");

            // Przekazywanie userId do widoku użytkownika
            ViewUtils.switchViewWithUserId((Stage) loginButton.getScene().getWindow(), "user-view.fxml",
                    controller -> {
                        if (controller instanceof UserController) {
                            ((UserController) controller).setUserId(userId);
                        }
                    });
        } else {
            System.out.println("Błędne dane logowania!");
            showAlert(Alert.AlertType.ERROR, "Błąd logowania", "Nieprawidłowy PESEL lub hasło.");
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
