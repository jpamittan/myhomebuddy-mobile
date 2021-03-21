package com.example.myhomebuddy.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String token;
    private String userId;
    private String first_name;
    private String middle_name;
    private String last_name;
    private String email;
    private String type;
    private Boolean is_activated;
    private String properties;
    private String profile_image;

    public LoggedInUser(
        String token,
        String userId,
        String first_name,
        String middle_name,
        String last_name,
        String email,
        String type,
        Boolean is_activated,
        String properties,
        String profile_image
    ) {
        this.token = token;
        this.userId = userId;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.email = email;
        this.type = type;
        this.is_activated = is_activated;
        this.properties = properties;
        this.profile_image = profile_image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIs_activated() {
        return is_activated;
    }

    public void setIs_activated(Boolean is_activated) {
        this.is_activated = is_activated;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}