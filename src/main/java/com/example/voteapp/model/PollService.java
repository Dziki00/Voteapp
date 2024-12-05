package com.example.voteapp.model;

import com.example.voteapp.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PollService {

    public boolean savePoll(String pollName, LocalDateTime startDate, LocalDateTime endDate,
                            String voivodeship, String municipality, List<Question> questions) {
        String pollInsertQuery = "INSERT INTO polls (name, start_date, end_date, voivodeship, municipality, is_active) VALUES (?, ?, ?, ?, ?, true) RETURNING id";
        String questionInsertQuery = "INSERT INTO questions (poll_id, question_text) VALUES (?, ?) RETURNING id";
        String optionInsertQuery = "INSERT INTO options (question_id, option_text) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pollStmt = conn.prepareStatement(pollInsertQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement questionStmt = conn.prepareStatement(questionInsertQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement optionStmt = conn.prepareStatement(optionInsertQuery)) {

            conn.setAutoCommit(false);

            int pollId = savePoll(pollStmt, pollName, startDate, endDate, voivodeship, municipality);
            saveQuestionsAndOptions(questionStmt, optionStmt, pollId, questions);

            conn.commit();
            return true;

        } catch (Exception e) {
            System.err.println("Błąd podczas zapisywania ankiety: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private int savePoll(PreparedStatement pollStmt, String pollName, LocalDateTime startDate,
                         LocalDateTime endDate, String voivodeship, String municipality) throws SQLException {
        pollStmt.setString(1, pollName);
        pollStmt.setTimestamp(2, Timestamp.valueOf(startDate));
        pollStmt.setTimestamp(3, Timestamp.valueOf(endDate));
        pollStmt.setString(4, voivodeship);
        pollStmt.setString(5, municipality);

        pollStmt.executeUpdate();
        try (ResultSet pollRs = pollStmt.getGeneratedKeys()) {
            if (pollRs.next()) {
                return pollRs.getInt(1);
            } else {
                throw new SQLException("Nie udało się pobrać ID ankiety.");
            }
        }
    }

    private void saveQuestionsAndOptions(PreparedStatement questionStmt, PreparedStatement optionStmt,
                                         int pollId, List<Question> questions) throws SQLException {
        for (Question question : questions) {
            int questionId = saveQuestion(questionStmt, pollId, question.getQuestionText());
            saveOptions(optionStmt, questionId, question.getOptions());
        }
    }

    private int saveQuestion(PreparedStatement questionStmt, int pollId, String questionText) throws SQLException {
        questionStmt.setInt(1, pollId);
        questionStmt.setString(2, questionText);
        questionStmt.executeUpdate();

        try (ResultSet questionRs = questionStmt.getGeneratedKeys()) {
            if (questionRs.next()) {
                return questionRs.getInt(1);
            } else {
                throw new SQLException("Nie udało się pobrać ID pytania.");
            }
        }
    }

    private void saveOptions(PreparedStatement optionStmt, int questionId, List<String> options) throws SQLException {
        for (String option : options) {
            optionStmt.setInt(1, questionId);
            optionStmt.setString(2, option);
            optionStmt.executeUpdate();
        }
    }

    public boolean hasUserVoted(int pollId, int userId) {
        String query = "SELECT COUNT(*) FROM votes WHERE poll_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, pollId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas sprawdzania głosu użytkownika: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveUserVote(int pollId, int userId, int questionId, String selectedOption) {
        String insertVoteQuery = "INSERT INTO votes (poll_id, user_id, question_id, selected_option) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(insertVoteQuery)) {

            stmt.setInt(1, pollId);
            stmt.setInt(2, userId);
            stmt.setInt(3, questionId);
            stmt.setString(4, selectedOption);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("Błąd podczas zapisywania głosu użytkownika: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean updatePoll(Poll poll) {
        String query = "UPDATE polls SET name = ?, start_date = ?, end_date = ?, voivodeship = ?, municipality = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, poll.getName());
            stmt.setTimestamp(2, Timestamp.valueOf(poll.getStartDate()));
            stmt.setTimestamp(3, Timestamp.valueOf(poll.getEndDate()));
            stmt.setString(4, poll.getVoivodeship());
            stmt.setString(5, poll.getMunicipality());
            stmt.setInt(6, poll.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Błąd podczas aktualizacji ankiety: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePoll(Poll poll) {
        String query = "DELETE FROM polls WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, poll.getId());
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.err.println("Błąd podczas usuwania ankiety: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean togglePollStatus(Poll poll) {
        String query = "UPDATE polls SET is_active = NOT is_active WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, poll.getId());
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                poll.setActive(!poll.isActive()); // Zaktualizuj status lokalnie
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas zmiany statusu ankiety: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Poll> getAllPolls() {
        String pollQuery = "SELECT * FROM polls";
        String questionQuery = "SELECT * FROM questions WHERE poll_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pollStmt = conn.prepareStatement(pollQuery);
             ResultSet pollRs = pollStmt.executeQuery()) {

            List<Poll> polls = new ArrayList<>();

            while (pollRs.next()) {
                Poll poll = new Poll(
                        pollRs.getInt("id"),
                        pollRs.getString("name"),
                        pollRs.getTimestamp("start_date").toLocalDateTime(),
                        pollRs.getTimestamp("end_date").toLocalDateTime(),
                        pollRs.getBoolean("is_active")
                );

                try (PreparedStatement questionStmt = conn.prepareStatement(questionQuery)) {
                    questionStmt.setInt(1, poll.getId());
                    try (ResultSet questionRs = questionStmt.executeQuery()) {

                        List<Question> questions = new ArrayList<>();
                        while (questionRs.next()) {
                            Question question = new Question(
                                    questionRs.getInt("id"),
                                    questionRs.getString("question_text"),
                                    getOptionsForQuestion(questionRs.getInt("id"), conn)
                            );
                            questions.add(question);
                        }
                        poll.setQuestions(questions);
                    }
                }

                polls.add(poll);
            }

            return polls;

        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania ankiet: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    private List<String> getOptionsForQuestion(int questionId, Connection conn) throws SQLException {
        String optionQuery = "SELECT option_text FROM options WHERE question_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(optionQuery)) {
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> options = new ArrayList<>();
                while (rs.next()) {
                    options.add(rs.getString("option_text"));
                }
                return options;
            }
        }
    }


}
