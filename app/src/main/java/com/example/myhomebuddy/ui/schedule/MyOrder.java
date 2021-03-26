package com.example.myhomebuddy.ui.schedule;

public class MyOrder {
    int id;
    int user_id;
    String image;
    String first_name;
    String middle_name;
    String last_name;
    String email;
    String contact;
    String city;

    public MyOrder(
        int id,
        int user_id,
        String image,
        String first_name,
        String middle_name,
        String last_name,
        String email,
        String contact,
        String city
    ) {
        this.id = id;
        this.user_id = user_id;
        this.image = image;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.email = email;
        this.contact = contact;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
