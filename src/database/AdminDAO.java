package database;

import model.Doctor;
import model.Nurse;
import model.Theater;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminDAO {

    public boolean addDoctor(Doctor d) {
        String sql = "INSERT INTO users (user_id, username, password, role) VALUES (?, ?, ?, ?)";
        // In a real system, you might have a separate 'doctors' table for specialty, name, contact
        // But based on the existing DB schema (if there is one) or creating a simple one:
        // We will just assume UserDAO handles basic registration, but AdminDAO handles full
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt1 = conn.prepareStatement(sql)) {
                stmt1.setString(1, d.getUserId());
                stmt1.setString(2, d.getUsername());
                stmt1.setString(3, d.getPassword());
                stmt1.setString(4, d.getRole());
                stmt1.executeUpdate();
            }
            
            String sqlDoc = "INSERT INTO doctors (doctor_id, full_name, contact_number, specialty) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt2 = conn.prepareStatement(sqlDoc)) {
                stmt2.setString(1, d.getUserId());
                stmt2.setString(2, d.getFullName());
                stmt2.setString(3, d.getContact());
                stmt2.setString(4, d.getSpecialty());
                stmt2.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addNurse(Nurse n) {
        String sql = "INSERT INTO users (user_id, username, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt1 = conn.prepareStatement(sql)) {
                stmt1.setString(1, n.getUserId());
                stmt1.setString(2, n.getUsername());
                stmt1.setString(3, n.getPassword());
                stmt1.setString(4, n.getRole());
                stmt1.executeUpdate();
            }
            
            String sqlNurse = "INSERT INTO nurses (nurse_id, full_name, contact_number) VALUES (?, ?, ?)";
            try (PreparedStatement stmt2 = conn.prepareStatement(sqlNurse)) {
                stmt2.setString(1, n.getUserId());
                stmt2.setString(2, n.getFullName());
                stmt2.setString(3, n.getContact());
                stmt2.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addTheater(Theater t) {
        String sql = "INSERT INTO theaters (theater_id, name) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getTheaterId());
            stmt.setString(2, t.getName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addOperation(model.Operation op) {
        String sql = "INSERT INTO operation_types (op_id, op_name, description) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, op.getOpId());
            stmt.setString(2, op.getOpName());
            stmt.setString(3, op.getDescription());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
