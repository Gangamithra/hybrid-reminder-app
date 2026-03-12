package com.example.locationreminder;

public class ReminderModel {
    private String title;
    private double lat;
    private double lon;
    private float radius;
    private String date;
    private String time;

    public ReminderModel(String title, double lat, double lon, float radius, String date, String time) {
        this.title = title;
        this.lat = lat;
        this.lon = lon;
        this.radius = radius;
        this.date = date;
        this.time = time;
    }

    // These are called "getters" and they allow other files to read the data
    public String getTitle() { return title; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public float getRadius() { return radius; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}
