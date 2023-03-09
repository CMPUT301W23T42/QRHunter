package com.example.qrhunter;

import android.location.Location;

import java.util.Date;

public class QRCode {
    private String date;
    private String hash;
    private String name;
    private Location location;
    private String owner;
    private int score;
    private String id;

    public QRCode(String date, String hash, String name, Location location, String owner, int score, String id) {
        this.date = date;
        this.hash = hash;
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.score = score;
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public String getOwner() {
        return owner;
    }

    public int getScore() {
        return score;
    }

    public String getId() {
        return id;
    }
}