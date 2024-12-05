package com.example.voteapp.model;

import java.time.LocalDateTime;
import java.util.List;

public class Poll {

    private int id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String voivodeship; // Województwo
    private String municipality; // Gmina
    private boolean isActive; // Pole isActive
    private List<Question> questions;

    // Konstruktor z pełnym zestawem pól
    public Poll(int id, String name, LocalDateTime startDate, LocalDateTime endDate, boolean isActive) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.voivodeship = voivodeship;
        this.municipality = municipality;
        this.isActive = isActive;
    }

    // Gettery
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getVoivodeship() {
        return voivodeship;
    }

    public String getMunicipality() {
        return municipality;
    }

    public boolean isActive() {
        return isActive;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    // Settery
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setVoivodeship(String voivodeship) {
        this.voivodeship = voivodeship;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    // Opcjonalne: Nadpisanie metody toString() (przydatne do debugowania)
    @Override
    public String toString() {
        return "Poll{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", voivodeship='" + voivodeship + '\'' +
                ", municipality='" + municipality + '\'' +
                ", isActive=" + isActive +
                ", questions=" + questions +
                '}';
    }
}
