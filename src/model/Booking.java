package model;

import java.sql.Date;
import java.sql.Time;

public class Booking {
    private String bookingId;
    private String patientId;
    private String doctorId;
    private String theaterId;
    private String opId;
    private Date date;
    private Time time;
    private String status;

    public Booking(String bookingId, String patientId, String doctorId, String theaterId, String opId, Date date, Time time, String status) {
        this.bookingId = bookingId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.theaterId = theaterId;
        this.opId = opId;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getTheaterId() { return theaterId; }
    public String getOpId() { return opId; }
    public Date getDate() { return date; }
    public Date getSurgeryDate() { return date; }
    public Time getTime() { return time; }
    public Time getStartTime() { return time; }
    public String getStatus() { return status; }
}
