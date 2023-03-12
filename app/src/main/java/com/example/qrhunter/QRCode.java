package com.example.qrhunter;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import java.util.Comparator;
import java.util.Date;

/**
 * This class defines a QR code.
 */
public class QRCode {
    private String date;
    private String hash;
    private String name;
    private GeoPoint location;
    private String owner;
    private int score;
    private String id;

    /**
     * This constructs a QRCode object from parameter
     * @param date
     *    date and time when QRCode was added
     * @param hash
     *    hash value of the QRCode
     * @param name
     *    name of QRCode generated from hash
     * @param location
     *    location where the QRCode was added
     * @param owner
     *    name of player who scanned the QRCode
     * @param score
     *    score of QRCode generated from hash
     * @param id
     *    id of document which stores the QRCode info in the database
     */
    public QRCode(String date, String hash, String name, GeoPoint location, String owner, int score, String id) {
        this.date = date;
        this.hash = hash;
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.score = score;
        this.id = id;
    }

    /**
     * This returns the date attribute of the QRCode
     * @return
     *    Return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * This returns the hash attribute of the QRCode
     * @return
     *    Return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * This returns the name attribute of the QRCode
     * @return
     *    Return the name of the QRCode
     */
    public String getName() {
        return name;
    }

    /**
     * This returns the location attribute of the QRCode
     * @return
     *    Return the location
     */
    public GeoPoint getLocation() {
        return location;
    }

    /**
     * This returns the owner attribute of the QRCode
     * @return
     *    Return the name of the player who scanned the QRCode
     */
    public String getOwner() {
        return owner;
    }

    /**
     * This returns the score attribute of the QRCode
     * @return
     *    Return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * This returns the id attribute of the QRCode
     * @return
     *    Return the id of the document which holds the QRCode info on the database
     */
    public String getId() {
        return id;
    }


    /**
     * Compares this a QRCode object with another QRCode based on the score value
     * @param qrCode
     *    the first object to be compared
     * @param t1
     *    the second object to be compared
     * @return
     *    a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    public static Comparator<QRCode> ascendingOrder = new Comparator<QRCode>() {
        @Override
        public int compare(QRCode qrCode, QRCode t1) {
            return qrCode.getScore() - t1.getScore();
        }
    };

    /**
     * Compares this a QRCode object with another QRCode based on the score value
     * @param qrCode
     *    the first object to be compared
     * @param t1
     *    the second object to be compared
     * @return
     *    a negative integer, zero, or a positive integer as the second argument is less than, equal to, or greater than the first.
     */
    public static Comparator<QRCode> descendingOrder = new Comparator<QRCode>() {
        @Override
        public int compare(QRCode qrCode, QRCode t1) {
            return t1.getScore() - qrCode.getScore();
        }
    };
}