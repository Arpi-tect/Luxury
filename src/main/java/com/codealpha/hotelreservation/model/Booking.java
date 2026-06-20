package com.codealpha.hotelreservation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Booking {
    @Id
    private String bookingId;
    private String guestName;
    private String contact;
    private int roomNumber;
    private int nights;
    private double totalCost;
    private String paymentStatus;

    public Booking() {}

    public Booking(String bookingId, String guestName, String contact, int roomNumber, int nights, double totalCost, String paymentStatus) {
        this.bookingId = bookingId;
        this.guestName = guestName;
        this.contact = contact;
        this.roomNumber = roomNumber;
        this.nights = nights;
        this.totalCost = totalCost;
        this.paymentStatus = paymentStatus;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }
    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
