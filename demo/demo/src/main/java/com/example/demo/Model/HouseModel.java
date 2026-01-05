package com.example.demo.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "houses")
public class HouseModel {

    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        DISABLED
    }

    @Id
    private String id;

    // vlasnik (HOST)
    private String hostId;

    // osnovni podaci
    private String title;
    private String description;

    // lokacija
    private String city;
    private String address;

    // kapacitet i cena
    private int maxGuests;
    private BigDecimal pricePerNight;

    // slike (URL-ovi)
    private List<String> imageUrls;

    // status odobravanja
    private Status status;

    // audit polja
    private Instant createdAt;
    private Instant approvedAt;
    private String approvedByAdminId;

    public HouseModel() {}

    // ======================
    // GETTERS & SETTERS
    // ======================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getApprovedByAdminId() {
        return approvedByAdminId;
    }

    public void setApprovedByAdminId(String approvedByAdminId) {
        this.approvedByAdminId = approvedByAdminId;
    }

    // ======================
    // HELPER METODE
    // ======================

    public boolean isApproved() {
        return this.status == Status.APPROVED;
    }

    public boolean isPending() {
        return this.status == Status.PENDING;
    }

    public boolean isDisabled() {
        return this.status == Status.DISABLED;
    }
}
