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
            System.out.println("Booking Failed: Slot is already taken.");
            return false;
        }

        // 2. Perform the Insert
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

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error: Duplicate Booking ID or Invalid Reference (Doctor/Patient/Theater ID not found).");
            // e.printStackTrace(); // Uncomment for debugging
            return false;
        }
    }

    public boolean addBooking(Booking b) {
        return addBooking(b.getBookingId(), b.getPatientId(), b.getDoctorId(), b.getTheaterId(), b.getOpId(), b.getSurgeryDate().toString(), b.getStartTime().toString());
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
                        rs.getString("op_id"),
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
                        rs.getString("op_id"),
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

    public boolean completeSurgery(model.PostOperation postOp) {
        String insertSql = "INSERT INTO post_operations (pop_id, booking_id, prescription_events, side_effects, medicine, next_date) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE bookings SET status = 'COMPLETED' WHERE booking_id = ?";
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt1 = conn.prepareStatement(insertSql)) {
                stmt1.setString(1, postOp.getPOpId());
                stmt1.setString(2, postOp.getBookingId());
                stmt1.setString(3, postOp.getPrescriptionEvents());
                stmt1.setString(4, postOp.getSideEffects());
                stmt1.setString(5, postOp.getMedicine());
                stmt1.setDate(6, postOp.getNextDate());
                stmt1.executeUpdate();
            }
            try (PreparedStatement stmt2 = conn.prepareStatement(updateSql)) {
                stmt2.setString(1, postOp.getBookingId());
                stmt2.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}