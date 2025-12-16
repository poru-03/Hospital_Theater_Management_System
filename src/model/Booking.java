package model;

import java.sql.Date; // Use SQL Date for databases
import java.sql.Time; // Use SQL Time

public class Booking {
    private String bookingId;
    private String patientId;
    private String doctorId;
    private String theaterId;
    private Date date;
    private Time time;
    private String status;

    public Booking(String bookingId, String patientId, String doctorId, String theaterId, Date date, Time time, String status) {
        this.bookingId = bookingId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.theaterId = theaterId;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getTheaterId() { return theaterId; }
    public Date getDate() { return date; }
    public Time getTime() { return time; }
}
