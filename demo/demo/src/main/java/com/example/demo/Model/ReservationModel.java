package com.example.demo.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "reservations")

public class ReservationModel {
    public enum Status { PENDING, APPROVED, REJECTED, CANCELLED }

    @Id
    private String id;

    private String houseId; // houses._id
    private String guestId; // users._id (role = GUEST)

    private LocalDate dateFrom;
    private LocalDate dateTo;

    private int guestsCount;

    private Status status;

    private Instant requestedAt;

    // approval/decision fields
    private Instant decidedAt;
    private String decidedByUserId; // HOST ili ADMIN
    private String decisionNote;

    public ReservationModel() {}

    public String getId() {
        return id;
    }

    public String getHouseId() {
        return houseId;
    }

    public String getGuestId() {
        return guestId;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public int getGuestsCount() {
        return guestsCount;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public String getDecidedByUserId() {
        return decidedByUserId;
    }

    public String getDecisionNote() {
        return decisionNote;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public void setGuestsCount(int guestsCount) {
        this.guestsCount = guestsCount;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public void setDecidedAt(Instant decidedAt) {
        this.decidedAt = decidedAt;
    }

    public void setDecidedByUserId(String decidedByUserId) {
        this.decidedByUserId = decidedByUserId;
    }

    public void setDecisionNote(String decisionNote) {
        this.decisionNote = decisionNote;
    }
}
