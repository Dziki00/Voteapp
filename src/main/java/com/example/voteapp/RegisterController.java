package com.example.voteapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;

public class RegisterController {

    @FXML
    private TextField peselField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField birthYearField;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    @FXML
    private ImageView logoSmall;

    @FXML
    private ComboBox<String> voivodeshipComboBox;
    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> {
            String pesel = peselField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String repeatPassword = repeatPasswordField.getText();
            String address = addressField.getText();
            String birthYear = birthYearField.getText();

            // Walidacja hasła
            if (!password.equals(repeatPassword)) {
                System.out.println("Hasła nie są zgodne!");
                return;
            }

            // Walidacja PESEL i rok urodzenia
            if (!pesel.matches("\\d{11}")) {
                System.out.println("PESEL musi składać się z 11 cyfr!");
                return;
            }

            if (!birthYear.matches("\\d{4}")) {
                System.out.println("Rok urodzenia musi składać się z 4 cyfr!");
                return;
            }

            // Wyświetlenie danych w konsoli (możesz zamienić na zapis do bazy danych)
            System.out.println("Rejestracja zakończona sukcesem:");
            System.out.println("PESEL: " + pesel);
            System.out.println("Email: " + email);
            System.out.println("Adres zameldowania: " + address);
            System.out.println("Rok urodzenia: " + birthYear);
        });

        backButton.setOnAction(event -> {
            ViewUtils.switchView((Stage) backButton.getScene().getWindow(), "start-view.fxml");
        });
        logoSmall.setImage(new Image(getClass().getResourceAsStream("/com/example/voteapp/logo.png")));

        voivodeshipComboBox.getItems().addAll(
                "Dolnośląskie", "Kujawsko-Pomorskie", "Lubelskie", "Lubuskie",
                "Łódzkie", "Małopolskie", "Mazowieckie", "Opolskie",
                "Podkarpackie", "Podlaskie", "Pomorskie", "Śląskie",
                "Świętokrzyskie", "Warmińsko-Mazurskie", "Wielkopolskie",
                "Zachodniopomorskie"
        );

    }
}
