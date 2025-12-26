package com.example.demo.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "ratingsComments")

public class RatingCommentModel {
    @Id
    private String id;

    private String houseId; // houses._id
    private String guestId; // users._id (role = GUEST)

    private int rating;     // 1-5
    private String comment;

    private Instant createdAt;

    public RatingCommentModel() {}

    public String getId() {
        return id;
    }

    public String getHouseId() {
        return houseId;
    }

    public String getGuestId() {
        return guestId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
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

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
