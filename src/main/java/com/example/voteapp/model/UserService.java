package com.example.voteapp.model;

import com.example.voteapp.utils.DatabaseConnection;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserService {

    public boolean registerUser(String pesel, String email, String password, String address, String voivodeship, String municipality, String birthDate) {
        // Haszowanie hasła
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO users (pesel, email, password, address, voivodeship, municipality, birth_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pesel);
            pstmt.setString(2, email);
            pstmt.setString(3, hashedPassword); // Zapisujemy zahaszowane hasło
            pstmt.setString(4, address);
            pstmt.setString(5, voivodeship);
            pstmt.setString(6, municipality);
            pstmt.setDate(7, java.sql.Date.valueOf(birthDate)); // Format: YYYY-MM-DD

            pstmt.executeUpdate();
            System.out.println("Użytkownik zarejestrowany pomyślnie.");
            return true;
        } catch (Exception e) {
            System.out.println("Błąd podczas rejestracji użytkownika.");
            e.printStackTrace();
            return false;
        }
    }


    public boolean loginUser(String pesel, String password) {
        String sql = "SELECT password FROM users WHERE pesel = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, pesel);

            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password");

                // Weryfikacja hasła
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    System.out.println("Logowanie udane.");
                    return true;
                } else {
                    System.out.println("Nieprawidłowe hasło.");
                }
            } else {
                System.out.println("Nie znaleziono użytkownika o podanym PESEL.");
            }

        } catch (Exception e) {
            System.out.println("Błąd podczas logowania użytkownika: " + e.getMessage());
        }
        return false; // W przypadku błędu lub nieudanej weryfikacji
    }
}
