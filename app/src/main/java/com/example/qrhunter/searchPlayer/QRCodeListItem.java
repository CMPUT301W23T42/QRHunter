package com.example.qrhunter.searchPlayer;

/** Class to make objects for array adapter used to implement listview in the searched player profile fragment **/
public class QRCodeListItem {
    private String name;
    private int score;

    public QRCodeListItem(String name, int score) {
        this.name = name;
        this.score = score;
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
}
