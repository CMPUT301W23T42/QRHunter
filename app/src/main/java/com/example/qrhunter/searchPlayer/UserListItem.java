package com.example.qrhunter.searchPlayer;

/** Class to make objects for array adapter used to implement listview in the leaderboard fragment **/
public class UserListItem {
    private String username;
    private int score;

    public UserListItem(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
