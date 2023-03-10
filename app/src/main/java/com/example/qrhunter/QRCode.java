package com.example.qrhunter;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import java.util.Comparator;
import java.util.Date;

public class QRCode {
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

    public static Comparator<QRCode> ascendingOrder = new Comparator<QRCode>() {
        @Override
        public int compare(QRCode qrCode, QRCode t1) {
            return qrCode.getScore() - t1.getScore();
        }
    };

    public static Comparator<QRCode> descendingOrder = new Comparator<QRCode>() {
        @Override
        public int compare(QRCode qrCode, QRCode t1) {
            return t1.getScore() - qrCode.getScore();
        }
    };
}