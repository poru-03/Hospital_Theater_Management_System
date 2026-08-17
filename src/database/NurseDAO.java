package database;

import model.Doctor;
import model.Theater;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NurseDAO {

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        // Note: the UI expects Doctor object with enough info for display.
        // E.g. getFullName and getSpecialty. We will query doctors table.
        // Since we don't have password, we can just put empty string for password.
        String sql = "SELECT * FROM doctors";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                doctors.add(new Doctor(
                    rs.getString("doctor_id"),
                    rs.getString("doctor_id"), // username as ID for display if missing
                    "", // password
                    rs.getString("full_name"),
                    rs.getString("contact_number"),
                    rs.getString("specialty")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    public List<Theater> getAllTheaters() {
        List<Theater> theaters = new ArrayList<>();
        String sql = "SELECT * FROM theaters";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                theaters.add(new Theater(
                    rs.getString("theater_id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return theaters;
    }

    public List<model.Operation> getAllOperations() {
        return new PatientDAO().getAllOperations();
    }
}
