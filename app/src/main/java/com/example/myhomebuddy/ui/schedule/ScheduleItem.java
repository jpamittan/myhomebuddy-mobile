package com.example.myhomebuddy.ui.schedule;

public class ScheduleItem {
    int qty;
    String month;
    int day;
    String time;
    String name;
    String image;

    public ScheduleItem(int qty, String month, int day, String time, String name, String image) {
        this.qty = qty;
        this.month = month;
        this.day = day;
        this.time = time;
        this.name = name;
        this.image = image;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }
}
