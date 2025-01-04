package com.example.voteapp.model;

import com.example.voteapp.utils.DatabaseConnection;
import com.example.voteapp.utils.EncryptionUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

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
            System.err.println("Error saving poll: " + e.getMessage());
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
                throw new SQLException("Failed to retrieve poll ID.");
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
                throw new SQLException("Failed to retrieve question ID.");
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

    public boolean hasUserVoted(int pollId, String userHash) {
        String sql = "SELECT COUNT(*) FROM votes WHERE poll_id = ? AND user_hash = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, pollId);
            statement.setString(2, userHash);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if user voted: " + e.getMessage());
        }

        return false;
    }




    public boolean saveVote(int pollId, String userHash, int questionId, int selectedOptionId) {
        String sql = "INSERT INTO votes (poll_id, user_hash, question_id, selected_option_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, pollId);
            statement.setString(2, userHash);
            statement.setInt(3, questionId);
            statement.setInt(4, selectedOptionId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saving vote: " + e.getMessage());
        }

        return false;
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
            System.err.println("Error updating poll: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePoll(Poll poll) {
        String deleteVotesQuery = "DELETE FROM votes WHERE poll_id = ?";
        String deleteOptionsQuery = "DELETE FROM options WHERE question_id IN (SELECT id FROM questions WHERE poll_id = ?)";
        String deleteQuestionsQuery = "DELETE FROM questions WHERE poll_id = ?";
        String deletePollQuery = "DELETE FROM polls WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement deleteVotesStmt = conn.prepareStatement(deleteVotesQuery);
             PreparedStatement deleteOptionsStmt = conn.prepareStatement(deleteOptionsQuery);
             PreparedStatement deleteQuestionsStmt = conn.prepareStatement(deleteQuestionsQuery);
             PreparedStatement deletePollStmt = conn.prepareStatement(deletePollQuery)) {

            conn.setAutoCommit(false);

            deleteVotesStmt.setInt(1, poll.getId());
            deleteVotesStmt.executeUpdate();

            deleteOptionsStmt.setInt(1, poll.getId());
            deleteOptionsStmt.executeUpdate();

            deleteQuestionsStmt.setInt(1, poll.getId());
            deleteQuestionsStmt.executeUpdate();

            deletePollStmt.setInt(1, poll.getId());
            int rowsDeleted = deletePollStmt.executeUpdate();

            conn.commit();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting poll: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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

    public Map<String, Integer> getResultsForQuestion(int questionId) {
        String query = """
                SELECT options.option_text, COUNT(votes.id) AS vote_count
                FROM options
                LEFT JOIN votes ON options.id = votes.selected_option_id
                WHERE options.question_id = ?
                GROUP BY options.option_text;
                """;

        Map<String, Integer> results = new HashMap<>();

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String optionText = rs.getString("option_text");
                int voteCount = rs.getInt("vote_count");
                results.put(optionText, voteCount);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving results for question: " + e.getMessage());
        }

        return results;
    }

    public int getOptionIdByText(int questionId, String optionText) {
        String sql = "SELECT id FROM options WHERE question_id = ? AND option_text = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, questionId);
            statement.setString(2, optionText);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving option ID: " + e.getMessage());
        }
        return -1;
    }

    public List<String> getQuestionsForPoll(int pollId) {
        String query = "SELECT question_text FROM questions WHERE poll_id = ?";
        List<String> questions = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, pollId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(rs.getString("question_text"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania pytań dla ankiety: " + e.getMessage());
        }

        return questions;
    }

    public int getQuestionIdByText(int pollId, String questionText) {
        String query = "SELECT id FROM questions WHERE poll_id = ? AND question_text = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, pollId);
            stmt.setString(2, questionText);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas pobierania ID pytania: " + e.getMessage());
        }
        return -1; // Zwraca -1, jeśli ID pytania nie zostanie znalezione
    }

    private List<String> getOptionsForQuestion(int questionId, Connection conn) throws SQLException {
        String optionQuery = "SELECT option_text FROM options WHERE question_id = ?";
        List<String> options = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(optionQuery)) {
            stmt.setInt(1, questionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    options.add(rs.getString("option_text"));
                }
            }
        }

        return options;
    }

    public boolean togglePollStatus(Poll poll) {
        String query = "UPDATE polls SET is_active = NOT is_active WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, poll.getId());
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                poll.setActive(!poll.isActive());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas zmiany statusu ankiety: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

}
