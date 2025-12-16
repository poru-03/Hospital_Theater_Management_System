package database;

import model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // === METHOD 1: CHECK AVAILABILITY (The "Collision Check") ===
    // Returns TRUE if the slot is FREE. Returns FALSE if there is a conflict.
    public boolean isSlotAvailable(String doctorId, String theaterId, String date, String time) {
        // LOGIC: Check if there is any 'SCHEDULED' booking for this Doctor OR this Theater at that time.
        String sql = "SELECT COUNT(*) FROM bookings WHERE surgery_date = ? AND start_time = ? AND (doctor_id = ? OR theater_id = ?) AND status = 'SCHEDULED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, date);      // e.g., "2023-12-25"
            stmt.setString(2, time);      // e.g., "10:00:00"
            stmt.setString(3, doctorId);  // Check Doctor
            stmt.setString(4, theaterId); // Check Theater

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                // If count is 0, it means NO bookings exist. Slot is safe.
                return count == 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false (safe side) if error occurs
    }

    // === METHOD 2: ADD BOOKING ===
    public boolean addBooking(String bookingId, String patientId, String doctorId, String theaterId, String opId, String date, String time) {

        // 1. First, Double Check Availability (Security Layer)
        if (!isSlotAvailable(doctorId, theaterId, date, time)) {
            System.out.println("Booking Failed: Slot already taken.");
            return false;
        }

        // 2. Insert the Booking
        String sql = "INSERT INTO bookings (booking_id, patient_id, doctor_id, theater_id, op_id, surgery_date, start_time, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'SCHEDULED')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bookingId);
            stmt.setString(2, patientId);
            stmt.setString(3, doctorId);
            stmt.setString(4, theaterId);
            stmt.setString(5, opId);
            stmt.setString(6, date);
            stmt.setString(7, time);

            return stmt.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Error: Duplicate Booking ID or Invalid Reference (Doctor/Patient/Theater ID not found).");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === METHOD 3: GET BOOKINGS FOR A SPECIFIC DOCTOR ===
    // Used for the Doctor Dashboard (My Schedule)
    public List<Booking> getBookingsByDoctor(String doctorId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE doctor_id = ? AND status = 'SCHEDULED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Booking(
                        rs.getString("booking_id"),
                        rs.getString("patient_id"),
                        rs.getString("doctor_id"),
                        rs.getString("theater_id"),
                        rs.getDate("surgery_date"),
                        rs.getTime("start_time"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // === METHOD 4: CANCEL BOOKING (For Emergencies) ===
    public boolean cancelBooking(String bookingId) {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, bookingId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === METHOD 5: GET BOOKING FOR PATIENT (For Patient Dashboard) ===
    // This allows the Patient to search their own details using their ID
    public Booking getBookingByPatient(String patientId) {
        String sql = "SELECT * FROM bookings WHERE patient_id = ? AND status != 'CANCELLED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Booking(
                        rs.getString("booking_id"),
                        rs.getString("patient_id"),
                        rs.getString("doctor_id"),
                        rs.getString("theater_id"),
                        rs.getDate("surgery_date"),
                        rs.getTime("start_time"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no booking found
    }

    // === METHOD 6: UPDATE MEDICAL NOTES (For Doctor Dashboard) ===
    // This allows the Doctor to save the prescription after surgery
    public boolean updateMedicalNotes(String bookingId, String prescription, String sideEffects) {
        String sql = "UPDATE bookings SET prescription = ?, side_effects = ?, status = 'COMPLETED' WHERE booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, prescription);
            stmt.setString(2, sideEffects);
            stmt.setString(3, bookingId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}