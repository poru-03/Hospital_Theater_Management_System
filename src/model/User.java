package model;

public class User {
    private String userId;      // Matches VARCHAR(6)
    private String username;
    private String role;        // "ADMIN", "DOCTOR", "NURSE"

    // Constructor
    public User(String userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // === VALIDATION LOGIC ===

    // 1. Validate User ID (e.g., "D001")
    public static boolean isValidUserId(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.out.println("Invalid User ID: Cannot be empty.");
            return false;
        }
        if (id.length() > 6) {
            System.out.println("Invalid User ID: Max 6 characters allowed (e.g., D001).");
            return false;
        }
        return true;
    }

    // 2. Validate Password (Simple check: Minimum 4 characters)
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 4) {
            System.out.println("Invalid Password: Must be at least 4 characters.");
            return false;
        }
        return true;
    }

    // 3. Validate Role
    public static boolean isValidRole(String role) {
        if (role == null) return false;

        // Check if the role matches one of our allowed types
        if (role.equalsIgnoreCase("ADMIN") ||
                role.equalsIgnoreCase("DOCTOR") ||
                role.equalsIgnoreCase("NURSE")) {
            return true;
        }

        System.out.println("Invalid Role: Must be ADMIN, DOCTOR, or NURSE.");
        return false;
    }

    // === Getters ===
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}