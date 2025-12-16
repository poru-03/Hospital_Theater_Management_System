import database.BookingDAO;
import database.PatientDAO;
import database.UserDAO;
import model.Booking;
import model.Operation;
import model.User;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("========== HOSPITAL SYSTEM FULL BACKEND TEST ==========");

        // 1. Initialize all DAOs
        UserDAO userDAO = new UserDAO();
        PatientDAO patientDAO = new PatientDAO();
        BookingDAO bookingDAO = new BookingDAO();

        // =============================================================
        // SCENARIO 1: ADMIN REGISTERS STAFF
        // =============================================================
        System.out.println("\n[SCENARIO 1] Admin Registering Staff...");
        boolean adminSuccess = userDAO.registerUser("A001", "admin", "123", "ADMIN");
        boolean nurseSuccess = userDAO.registerUser("N001", "nurse1", "123", "NURSE");
        boolean docSuccess = userDAO.registerUser("D001", "doc1", "123", "DOCTOR");

        System.out.println("   -> Admin Added? " + (adminSuccess ? "Yes" : "Already Exists"));
        System.out.println("   -> Nurse Added? " + (nurseSuccess ? "Yes" : "Already Exists"));
        System.out.println("   -> Doctor Added? " + (docSuccess ? "Yes" : "Already Exists"));

        // =============================================================
        // SCENARIO 2: NURSE LOGS IN & PREPARES DATA
        // =============================================================
        System.out.println("\n[SCENARIO 2] Nurse Login & Setup...");
        User nurse = userDAO.login("nurse1", "123");
        if (nurse != null) {
            System.out.println("   ✅ Login Success: Nurse " + nurse.getUsername());

            // Fetch Operations for Dropdown
            List<Operation> ops = patientDAO.getAllOperations();
            if (!ops.isEmpty()) {
                System.out.println("   ✅ Operations Loaded: " + ops.size() + " types found.");
            } else {
                System.out.println("   ❌ Error: No Operations found in DB.");
            }
        } else {
            System.out.println("   ❌ Nurse Login Failed.");
        }

        // =============================================================
        // SCENARIO 3: NURSE REGISTERS A PATIENT
        // =============================================================
        System.out.println("\n[SCENARIO 3] Registering Patient...");
        // ID: P100, Name: John, Age: 45, Phone: 0771234567, Gender: Male, Op: OP001, Nurse: N001
        // (Make sure OP001 exists in your database!)
        boolean pAdded = patientDAO.addPatient("P100", "John Doe", 45, "0771234567", "Male", "OP001", "N001");
        if (pAdded) {
            System.out.println("   ✅ Patient P100 Registered.");
        } else {
            System.out.println("   ⚠️ Patient P100 might already exist.");
        }

        // =============================================================
        // SCENARIO 4: NURSE BOOKS SURGERY
        // =============================================================
        System.out.println("\n[SCENARIO 4] Booking Surgery...");
        String date = "2025-05-20";
        String time = "09:00:00";
        String theater = "T01"; // Ensure T01 exists in DB

        // Check availability
        if (bookingDAO.isSlotAvailable("D001", theater, date, time)) {
            boolean booked = bookingDAO.addBooking("B100", "P100", "D001", theater, "OP001", date, time);
            if (booked) {
                System.out.println("   ✅ Booking B100 Confirmed!");
            } else {
                System.out.println("   ❌ Booking Failed (SQL Error).");
            }
        } else {
            System.out.println("   ⚠️ Slot Busy! Cannot Book.");
        }

        // =============================================================
        // SCENARIO 5: PATIENT CHECKING DASHBOARD
        // =============================================================
        System.out.println("\n[SCENARIO 5] Patient Checking Dashboard...");
        Booking myBooking = bookingDAO.getBookingByPatient("P100");
        if (myBooking != null) {
            System.out.println("   ✅ Found Surgery for P100:");
            System.out.println("      - Date: " + myBooking.getDate());
            System.out.println("      - Doctor: " + myBooking.getDoctorId());
            System.out.println("      - Theater: " + myBooking.getTheaterId());
        } else {
            System.out.println("   ❌ No booking found for P100.");
        }

        System.out.println("\n========== TEST COMPLETE ==========");
    }
}
