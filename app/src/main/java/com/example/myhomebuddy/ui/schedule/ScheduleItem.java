package com.example.myhomebuddy.ui.schedule;

public class ScheduleItem {
    int id;
    String date;
    String time;
    float price;
    int qty;
    String status;

    public ScheduleItem(int id, String date, String time, float price, int qty, String status) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.price = price;
        this.qty = qty;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
