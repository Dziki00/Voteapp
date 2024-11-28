package com.example.voteapp.controllers;

import com.example.voteapp.utils.ViewUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ManageUsersController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button backButton;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> peselColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TableColumn<User, String> addressColumn;

    @FXML
    private TableColumn<User, String> voivodeshipColumn;

    @FXML
    private TableColumn<User, String> municipalityColumn;

    @FXML
    private TableColumn<User, String> birthDateColumn;

    private ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Inicjalizacja kolumn tabeli
        peselColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        voivodeshipColumn.setCellValueFactory(new PropertyValueFactory<>("voivodeship"));
        municipalityColumn.setCellValueFactory(new PropertyValueFactory<>("municipality"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        // Przykładowe dane użytkowników
        usersList.addAll(
                new User("12345678901", "jan.kowalski@example.com", "password123", "Warszawa, ul. Zielona 10", "Mazowieckie", "Warszawa", "1985-06-15"),
                new User("09876543210", "anna.nowak@example.com", "pass1234", "Kraków, ul. Lipowa 15", "Małopolskie", "Kraków", "1990-02-20"),
                new User("11122233344", "piotr.zielinski@example.com", "qwerty123", "Gdańsk, ul. Morska 5", "Pomorskie", "Gdańsk", "1987-11-03")
        );

        usersTable.setItems(usersList);

        // Obsługa przycisku wyszukiwania
        searchButton.setOnAction(event -> {
            String searchText = searchField.getText().toLowerCase();
            ObservableList<User> filteredList = FXCollections.observableArrayList();

            for (User user : usersList) {
                if (user.getPesel().toLowerCase().contains(searchText) ||
                        user.getEmail().toLowerCase().contains(searchText) ||
                        user.getAddress().toLowerCase().contains(searchText) ||
                        user.getVoivodeship().toLowerCase().contains(searchText) ||
                        user.getMunicipality().toLowerCase().contains(searchText) ||
                        user.getBirthDate().toLowerCase().contains(searchText)) {
                    filteredList.add(user);
                }
            }

            usersTable.setItems(filteredList);
        });

        // Obsługa przycisku edytowania użytkownika
        editButton.setOnAction(event -> {
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                System.out.println("Edytuj użytkownika: " + selectedUser);
                // Dodaj logikę edytowania użytkownika
            }
        });

        // Obsługa przycisku usuwania użytkownika
        deleteButton.setOnAction(event -> {
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                System.out.println("Usuń użytkownika: " + selectedUser);
                usersList.remove(selectedUser);
                usersTable.setItems(usersList);
            }
        });

        // Obsługa przycisku powrotu
        backButton.setOnAction(event -> {
            System.out.println("Powrót do panelu administratora");
            ViewUtils.switchView((Stage) backButton.getScene().getWindow(), "admin-view.fxml");
        });
    }

    // Klasa User (model danych dla tabeli)
    public static class User {
        private final String pesel;
        private final String email;
        private final String password;
        private final String address;
        private final String voivodeship;
        private final String municipality;
        private final String birthDate;

        public User(String pesel, String email, String password, String address, String voivodeship, String municipality, String birthDate) {
            this.pesel = pesel;
            this.email = email;
            this.password = password;
            this.address = address;
            this.voivodeship = voivodeship;
            this.municipality = municipality;
            this.birthDate = birthDate;
        }

        public String getPesel() {
            return pesel;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getAddress() {
            return address;
        }

        public String getVoivodeship() {
            return voivodeship;
        }

        public String getMunicipality() {
            return municipality;
        }

        public String getBirthDate() {
            return birthDate;
        }

        @Override
        public String toString() {
            return "User{" +
                    "pesel='" + pesel + '\'' +
                    ", email='" + email + '\'' +
                    ", password='" + password + '\'' +
                    ", address='" + address + '\'' +
                    ", voivodeship='" + voivodeship + '\'' +
                    ", municipality='" + municipality + '\'' +
                    ", birthDate='" + birthDate + '\'' +
                    '}';
        }
    }
}
