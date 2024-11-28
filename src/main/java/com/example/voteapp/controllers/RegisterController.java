package com.example.voteapp.controllers;

import com.example.voteapp.utils.DatabaseConnection;
import com.example.voteapp.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
    private TextField birthYearDate;

    @FXML
    private ComboBox<String> voivodeshipComboBox;

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    @FXML
    private ImageView logoSmall;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> {
            String pesel = peselField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String repeatPassword = repeatPasswordField.getText();
            String address = addressField.getText();
            String birthDateInput = birthYearDate.getText();
            String voivodeship = voivodeshipComboBox.getValue();

            // Walidacja daty
            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(birthDateInput, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Nieprawidłowy format daty. Użyj formatu DD.MM.YYYY.");
                return;
            }

            // Walidacja haseł
            if (!password.equals(repeatPassword)) {
                System.out.println("Hasła nie są zgodne!");
                return;
            }

            // Haszowanie hasła
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Walidacja PESEL
            if (!pesel.matches("\\d{11}")) {
                System.out.println("PESEL musi składać się z 11 cyfr!");
                return;
            }

            // Sprawdzenie województwa
            if (voivodeship == null || voivodeship.isEmpty()) {
                System.out.println("Proszę wybrać województwo!");
                return;
            }

            // Zapis danych do bazy danych
            try (Connection connection = DatabaseConnection.connect()) {
                String insertQuery = "INSERT INTO users (pesel, email, password, address, birth_date, voivodeship) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, pesel);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, hashedPassword); // Przechowujemy zahaszowane hasło
                preparedStatement.setString(4, address);
                preparedStatement.setDate(5, Date.valueOf(birthDate)); // Konwersja na SQL Date
                preparedStatement.setString(6, voivodeship);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Rejestracja zakończona sukcesem.");
                    ViewUtils.switchView((Stage) registerButton.getScene().getWindow(), "start-view.fxml");
                } else {
                    System.out.println("Wystąpił błąd podczas rejestracji.");
                }
            } catch (Exception e) {
                System.out.println("Błąd podczas zapisu do bazy danych: " + e.getMessage());
            }
        });

        backButton.setOnAction(event -> ViewUtils.switchView((Stage) backButton.getScene().getWindow(), "start-view.fxml"));

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
