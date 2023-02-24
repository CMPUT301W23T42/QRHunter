package com.example.qrhunter;

public class QRComment{
    private String comment;
    private String date;
    private String user;

    public QRComment(String comment, String date, String user) {
        this.comment = comment;
        this.date = date;
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getUser() {
        return user;
    }


}
