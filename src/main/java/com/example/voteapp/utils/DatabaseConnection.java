package com.example.voteapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/voteapp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "jesthaslo123";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Nie udało się połączyć z bazą danych.");
            e.printStackTrace();
            return null;
        }
    }
}
