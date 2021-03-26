package com.example.myhomebuddy;

public class Search {
    int id;
    String name;
    String description;
    float ratings;
    String image;

    public Search(int id, String name, String description, float ratings, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ratings = ratings;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRatings() {
        return ratings;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
