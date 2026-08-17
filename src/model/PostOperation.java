package model;

import java.sql.Date;

public class PostOperation {
    private String pOpId;
    private String bookingId;
    private String prescriptionEvents;
    private String sideEffects;
    private String medicine;
    private Date nextDate;

    public PostOperation(String pOpId, String bookingId, String prescriptionEvents, String sideEffects, String medicine, Date nextDate) {
        this.pOpId = pOpId;
        this.bookingId = bookingId;
        this.prescriptionEvents = prescriptionEvents;
        this.sideEffects = sideEffects;
        this.medicine = medicine;
        this.nextDate = nextDate;
    }

    public String getPOpId() { return pOpId; }
    public String getBookingId() { return bookingId; }
    public String getPrescriptionEvents() { return prescriptionEvents; }
    public String getPrescription() { return prescriptionEvents; }
    public String getSideEffects() { return sideEffects; }
    public String getMedicine() { return medicine; }
    public Date getNextDate() { return nextDate; }
    public Date getNextClinicDate() { return nextDate; }
}
