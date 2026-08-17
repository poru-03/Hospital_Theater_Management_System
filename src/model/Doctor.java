package model;

public class Doctor extends User {
    private String password;
    private String fullName;
    private String contact;
    private String specialty;

    public Doctor(String doctorId, String username, String password, String fullName, String contact, String specialty) {
        super(doctorId, username, "DOCTOR");
        this.password = password;
        this.fullName = fullName;
        this.contact = contact;
        this.specialty = specialty;
    }

    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getContact() { return contact; }
    public String getSpecialty() { return specialty; }
    public String getDoctorId() { return getUserId(); }
    
    @Override
    public String toString() {
        // Used in JComboBox display
        return fullName + " (" + specialty + ")";
    }
}
