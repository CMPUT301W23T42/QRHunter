package com.example.qrhunter;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

/**
 * This class defines a QR code.
 */
public class QRCode implements Comparable<QRCode>{
    private String date;
    private String hash;
    private String name;
    private GeoPoint location;
    private String owner;
    private int score;
    private String id;

    public QRCode(String date, String hash, String name, GeoPoint location, String owner, int score, String id) {
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

    public GeoPoint getLocation() {
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

    @Override
    public int compareTo(QRCode qrCode) {
        return this.score - qrCode.getScore();
    }
}