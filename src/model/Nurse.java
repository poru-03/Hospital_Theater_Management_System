package model;

public class Nurse extends User {
    private String password;
    private String fullName;
    private String contact;

    public Nurse(String nurseId, String username, String password, String fullName, String contact) {
        super(nurseId, username, "NURSE");
        this.password = password;
        this.fullName = fullName;
        this.contact = contact;
    }

    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getContact() { return contact; }
}
