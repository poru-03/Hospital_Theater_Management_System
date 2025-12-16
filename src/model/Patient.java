package model;

public class Patient {
    // 1. Fields matching the Database Columns
    private String patientId;
    private String name;            // Matches 'full_name' in DB
    private int age;
    private String contactNumber;
    private String gender;          // <--- ADDED (Matches 'gender' in DB)
    private String opId;
    private String assignedByNurseId; // <--- ADDED (Matches 'assigned_by_nurse_id')

    // 2. Updated Constructor (Now accepts 7 values)
    public Patient(String patientId, String name, int age, String contactNumber, String gender, String opId, String assignedByNurseId) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.contactNumber = contactNumber;
        this.gender = gender;
        this.opId = opId;
        this.assignedByNurseId = assignedByNurseId;
    }

    // 3. LOGIC: Contact Number Validation
    public static boolean isValidContact(String contact) {
        // Step 1: Check if it is empty (Null check must be first!)
        if (contact == null) {
            System.out.println("Invalid: Contact is empty.");
            return false;
        }

        // Step 2: Check length (Must be exactly 10)
        if (contact.length() != 10) {
            System.out.println("Invalid: Length must be 10.");
            return false;
        }

        // Step 3: Check if it starts with '0'
        if (contact.charAt(0) != '0') {
            System.out.println("Invalid: Must start with 0.");
            return false;
        }

        // Step 4: Check if all characters are numbers
        for (int i = 0; i < contact.length(); i++) {
            char c = contact.charAt(i);
            if (!Character.isDigit(c)) {
                System.out.println("Invalid: Must contain only numbers.");
                return false;
            }
        }

        return true; // If we reach here, it's valid!
    }

    // 4. Getters
    public String getPatientId() { return patientId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getContactNumber() { return contactNumber; }
    public String getGender() { return gender; }          // <--- NEW
    public String getOpId() { return opId; }
    public String getAssignedByNurseId() { return assignedByNurseId; } // <--- NEW
}