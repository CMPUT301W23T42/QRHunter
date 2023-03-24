package com.example.qrhunter.searchPlayer;

/** Class to make objects for array adapter used to implement listview in the searched player profile fragment **/
public class QRCodeListItem {
    private String name;
    private int score;
    private String hash;

    public QRCodeListItem(String name, int score, String hash) {
        this.name = name;
        this.score = score;
        this.hash = hash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
