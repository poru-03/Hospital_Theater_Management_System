package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBSeeder {

    public static void main(String[] args) {
        System.out.println("========== DATABASE SEEDER RUNNING ==========");
        seed();
        System.out.println("========== DATABASE SEEDING COMPLETED ==========");
    }

    public static void seed() {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Failed to connect to database. Seeding aborted.");
                return;
            }

            // 1. Drop existing tables and recreate schema to ensure correct columns
            try (Statement stmt = conn.createStatement()) {
                System.out.println("Dropping and rebuilding database schema...");
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
                stmt.execute("DROP TABLE IF EXISTS post_operations");
                stmt.execute("DROP TABLE IF EXISTS bookings");
                stmt.execute("DROP TABLE IF EXISTS patients");
                stmt.execute("DROP TABLE IF EXISTS doctors");
                stmt.execute("DROP TABLE IF EXISTS nurses");
                stmt.execute("DROP TABLE IF EXISTS users");
                stmt.execute("DROP TABLE IF EXISTS theaters");
                stmt.execute("DROP TABLE IF EXISTS operation_types");
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
                System.out.println("✅ Old tables dropped.");
                
                // users
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "user_id VARCHAR(6) PRIMARY KEY, " +
                        "username VARCHAR(50) NOT NULL UNIQUE, " +
                        "password VARCHAR(50) NOT NULL, " +
                        "role VARCHAR(20) NOT NULL" +
                        ")");

                // nurses
                stmt.execute("CREATE TABLE IF NOT EXISTS nurses (" +
                        "nurse_id VARCHAR(6) PRIMARY KEY, " +
                        "full_name VARCHAR(100) NOT NULL, " +
                        "contact_number VARCHAR(15), " +
                        "FOREIGN KEY (nurse_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                        ")");

                // doctors
                stmt.execute("CREATE TABLE IF NOT EXISTS doctors (" +
                        "doctor_id VARCHAR(6) PRIMARY KEY, " +
                        "full_name VARCHAR(100) NOT NULL, " +
                        "contact_number VARCHAR(15), " +
                        "specialty VARCHAR(50), " +
                        "FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE" +
                        ")");

                // theaters
                stmt.execute("CREATE TABLE IF NOT EXISTS theaters (" +
                        "theater_id VARCHAR(6) PRIMARY KEY, " +
                        "name VARCHAR(50) NOT NULL" +
                        ")");

                // operation_types
                stmt.execute("CREATE TABLE IF NOT EXISTS operation_types (" +
                        "op_id VARCHAR(6) PRIMARY KEY, " +
                        "op_name VARCHAR(50) NOT NULL, " +
                        "description TEXT" +
                        ")");

                // patients
                stmt.execute("CREATE TABLE IF NOT EXISTS patients (" +
                        "patient_id VARCHAR(6) PRIMARY KEY, " +
                        "full_name VARCHAR(100) NOT NULL, " +
                        "age INT, " +
                        "contact_number VARCHAR(15), " +
                        "gender VARCHAR(10), " +
                        "op_id VARCHAR(6), " +
                        "assigned_by_nurse_id VARCHAR(6), " +
                        "FOREIGN KEY (op_id) REFERENCES operation_types(op_id) ON DELETE SET NULL, " +
                        "FOREIGN KEY (assigned_by_nurse_id) REFERENCES users(user_id) ON DELETE SET NULL" +
                        ")");

                // bookings
                stmt.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                        "booking_id VARCHAR(6) PRIMARY KEY, " +
                        "patient_id VARCHAR(6), " +
                        "doctor_id VARCHAR(6), " +
                        "theater_id VARCHAR(6), " +
                        "op_id VARCHAR(6), " +
                        "surgery_date DATE, " +
                        "start_time TIME, " +
                        "status VARCHAR(20) DEFAULT 'SCHEDULED', " +
                        "FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (theater_id) REFERENCES theaters(theater_id) ON DELETE CASCADE, " +
                        "FOREIGN KEY (op_id) REFERENCES operation_types(op_id) ON DELETE CASCADE" +
                        ")");

                // post_operations
                stmt.execute("CREATE TABLE IF NOT EXISTS post_operations (" +
                        "pop_id VARCHAR(6) PRIMARY KEY, " +
                        "booking_id VARCHAR(6), " +
                        "prescription_events TEXT, " +
                        "side_effects TEXT, " +
                        "medicine TEXT, " +
                        "next_date DATE, " +
                        "FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE" +
                        ")");
                
                System.out.println("✅ Schema validated/created.");
            }

            // 2. Clear Tables (Clean Slate)
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
                stmt.execute("TRUNCATE TABLE post_operations");
                stmt.execute("TRUNCATE TABLE bookings");
                stmt.execute("TRUNCATE TABLE patients");
                stmt.execute("TRUNCATE TABLE doctors");
                stmt.execute("TRUNCATE TABLE nurses");
                stmt.execute("TRUNCATE TABLE users");
                stmt.execute("TRUNCATE TABLE theaters");
                stmt.execute("TRUNCATE TABLE operation_types");
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
                System.out.println("✅ Existing data cleared.");
            }

            // 3. Seed Theaters
            String sqlTheater = "INSERT INTO theaters (theater_id, name) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlTheater)) {
                String[][] theaters = {
                    {"T01", "Operating Room 1 (General Surgery)"},
                    {"T02", "Operating Room 2 (Cardiothoracic)"},
                    {"T03", "Operating Room 3 (Orthopedic)"},
                    {"T04", "Operating Room 4 (Neurosurgery)"},
                    {"T05", "Operating Room 5 (Pediatric)"}
                };
                for (String[] t : theaters) {
                    stmt.setString(1, t[0]);
                    stmt.setString(2, t[1]);
                    stmt.executeUpdate();
                }
                System.out.println("✅ Operating Theaters seeded.");
            }

            // 4. Seed Operation Types
            String sqlOp = "INSERT INTO operation_types (op_id, op_name, description) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlOp)) {
                String[][] ops = {
                    {"OP001", "Appendectomy", "Surgical removal of the appendix. Common general surgery procedure."},
                    {"OP002", "Coronary Bypass Grafting", "CABG surgery to improve blood flow to heart muscle."},
                    {"OP003", "Knee Joint Replacement", "Arthroplasty to replace damaged knee joint with implants."},
                    {"OP004", "Craniotomy", "Brain surgery performed by cutting a hole in the skull."},
                    {"OP005", "Pediatric Hernia Repair", "Correction of groin herniation in infants and children."},
                    {"OP006", "Rhinoplasty Surgery", "Reconstruction and aesthetic correction of the nose."}
                };
                for (String[] o : ops) {
                    stmt.setString(1, o[0]);
                    stmt.setString(2, o[1]);
                    stmt.setString(3, o[2]);
                    stmt.executeUpdate();
                }
                System.out.println("✅ Operation Types seeded.");
            }

            // 5. Seed default Nurse & Admin accounts
            String sqlUser = "INSERT INTO users (user_id, username, password, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUser)) {
                // Admin
                stmt.setString(1, "A001");
                stmt.setString(2, "admin");
                stmt.setString(3, "123");
                stmt.setString(4, "ADMIN");
                stmt.executeUpdate();

                // Nurse
                stmt.setString(1, "N001");
                stmt.setString(2, "nurse1");
                stmt.setString(3, "123");
                stmt.setString(4, "NURSE");
                stmt.executeUpdate();
            }
            String sqlNurse = "INSERT INTO nurses (nurse_id, full_name, contact_number) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlNurse)) {
                stmt.setString(1, "N001");
                stmt.setString(2, "Nurse Florence");
                stmt.setString(3, "0771112223");
                stmt.executeUpdate();
                System.out.println("✅ Admin & Nurse login records seeded.");
            }

            // 6. Seed Doctors for every surgeon type (General, Cardio, Ortho, Neuro, Pediatric, Plastic)
            String[][] doctors = {
                {"D001", "doc1", "123", "Dr. Albert Einstein", "0772223334", "General Surgery"},
                {"D002", "doc_cardio", "123", "Dr. Charles Drew", "0773334445", "Cardiothoracic Surgery"},
                {"D003", "doc_ortho", "123", "Dr. Oscar Bonfiglio", "0774445556", "Orthopedic Surgery"},
                {"D004", "doc_neuro", "123", "Dr. Norman Dott", "0775556667", "Neurosurgery"},
                {"D005", "doc_ped", "123", "Dr. Pamela Sande", "0776667778", "Pediatric Surgery"},
                {"D006", "doc_plastic", "123", "Dr. Patricia Era", "0777778889", "Plastic Surgery"}
            };

            for (String[] d : doctors) {
                // Insert User login record
                try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser)) {
                    stmtUser.setString(1, d[0]);
                    stmtUser.setString(2, d[1]);
                    stmtUser.setString(3, d[2]);
                    stmtUser.setString(4, "DOCTOR");
                    stmtUser.executeUpdate();
                }

                // Insert Doctor specialty profile
                String sqlDoc = "INSERT INTO doctors (doctor_id, full_name, contact_number, specialty) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmtDoc = conn.prepareStatement(sqlDoc)) {
                    stmtDoc.setString(1, d[0]);
                    stmtDoc.setString(2, d[3]);
                    stmtDoc.setString(3, d[4]);
                    stmtDoc.setString(4, d[5]);
                    stmtDoc.executeUpdate();
                }
            }
            System.out.println("✅ Doctors for all surgical specialties seeded.");

            // 7. Seed one dummy Patient & Booking for immediate test visualization
            String sqlPatient = "INSERT INTO patients (patient_id, full_name, age, contact_number, gender, op_id, assigned_by_nurse_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPatient)) {
                stmt.setString(1, "P100");
                stmt.setString(2, "John Doe");
                stmt.setInt(3, 45);
                stmt.setString(4, "0771234567");
                stmt.setString(5, "Male");
                stmt.setString(6, "OP001"); // General surgery Appendectomy
                stmt.setString(7, "N001");
                stmt.executeUpdate();
                System.out.println("✅ Sample Patient (P100) registered.");
            }

            String sqlBooking = "INSERT INTO bookings (booking_id, patient_id, doctor_id, theater_id, op_id, surgery_date, start_time, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlBooking)) {
                stmt.setString(1, "B100");
                stmt.setString(2, "P100");
                stmt.setString(3, "D001"); // Dr. Albert
                stmt.setString(4, "T01");  // OR 1
                stmt.setString(5, "OP001");
                stmt.setString(6, "2026-08-20");
                stmt.setString(7, "09:00:00");
                stmt.setString(8, "SCHEDULED");
                stmt.executeUpdate();
                System.out.println("✅ Sample Surgery Booking (B100) scheduled.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
