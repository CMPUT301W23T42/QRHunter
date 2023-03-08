package com.example.qrhunter;

import android.location.Location;

import java.util.Date;

public class QRCode {
    private String name;
    private String date;
    private String owner;
    private int id;
    private Location location;

    public QRCode(String name, String date, String owner, int id, Location location) {
        this.name = name;
        this.date = date;
        this.owner = owner;
        this.id = id;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String  date) {
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}