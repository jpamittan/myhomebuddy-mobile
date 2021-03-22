package com.example.myhomebuddy.ui.reviews;

import org.json.JSONObject;

public class Reviews {
    int id;
    JSONObject user;
    Float ratings;
    String message;
    String created_at;

    public Reviews(
        int id,
        JSONObject user,
        Float ratings,
        String message,
        String created_at
    ) {
        this.id = id;
        this.user = user;
        this.ratings = ratings;
        this.message = message;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JSONObject getUser() {
        return user;
    }

    public void setUser(JSONObject user) {
        this.user = user;
    }

    public Float getRatings() {
        return ratings;
    }

    public void setRatings(Float ratings) {
        this.ratings = ratings;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
