package database;

import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class UserDAO {

    // === METHOD 1: LOGIN ===
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // User found! Return the object.
                return new User(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Login Error: Database connection failed.");
            e.printStackTrace();
        }
        return null; // Return null if login fails
    }

    // === METHOD 2: REGISTER USER ===
    public boolean registerUser(String userId, String username, String password, String role) {
        // Note: 'status' column is removed from SQL as per your request
        String sql = "INSERT INTO users (user_id, username, password, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, role);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // === EXCEPTION HANDLING: DUPLICATES ===
            // This runs if User ID or Username already exists in the database
            System.err.println("Registration Failed: Username '" + username + "' or ID '" + userId + "' already exists.");
            return false;

        } catch (SQLException e) {
            // === EXCEPTION HANDLING: GENERAL ===
            System.err.println("Database Error during registration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}