package database;

import model.Operation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // === METHOD 1: GET OPERATION LIST ===
    // Used to fill the Dropdown menu in the Nurse Dashboard
    public List<Operation> getAllOperations() {
        List<Operation> list = new ArrayList<>(); // Create an empty list
        String sql = "SELECT * FROM operation_types";

        // Try-with-resources: Auto-closes the connection
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop through every row in the database table
            while (rs.next()) {
                list.add(new Operation(
                        rs.getString("op_id"),
                        rs.getString("op_name"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            // Error Handling: Print specific message to console
            System.err.println("Error fetching operations list: " + e.getMessage());
            e.printStackTrace();
        }
        return list; // Return the list (might be empty if error occurred)
    }

    // === METHOD 2: ADD NEW PATIENT ===
    // Returns TRUE if successful, FALSE if it failed
    public boolean addPatient(String patientId, String name, int age, String contact, String gender, String opId, String nurseId) {

        // The SQL Command to insert data
        String sql = "INSERT INTO patients (patient_id, full_name, age, contact_number, gender, op_id, assigned_by_nurse_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Binding the variables to the '?' marks
            stmt.setString(1, patientId);
            stmt.setString(2, name);
            stmt.setInt(3, age);
            stmt.setString(4, contact);
            stmt.setString(5, gender);  // Matches database column 'gender'
            stmt.setString(6, opId);    // Matches database column 'op_id'
            stmt.setString(7, nurseId); // Matches database column 'assigned_by_nurse_id'

            // Execute the update. If rows > 0, it means 1 row was added.
            return stmt.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            // === EXCEPTION HANDLING LEVEL 1: LOGIC ERRORS ===
            // This specific error happens if:
            // 1. The Patient ID (P001) already exists (Duplicate Key).
            // 2. The opId or nurseId does not exist in the database (Foreign Key failure).
            System.err.println("Failed to add patient. Reason: Duplicate Patient ID or Invalid Operation/Nurse ID.");
            return false;

        } catch (SQLException e) {
            // === EXCEPTION HANDLING LEVEL 2: SYSTEM ERRORS ===
            // This happens if the database is down, password is wrong, or SQL syntax is bad.
            System.err.println("Critical Database Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}