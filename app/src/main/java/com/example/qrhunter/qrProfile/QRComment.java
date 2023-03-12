package com.example.qrhunter.qrProfile;

/**
 * This class defines a QR code comment
 */
public class QRComment{
    private String comment;
    private String date;
    private String user;

    public QRComment(String comment, String date, String user) {
        this.comment = comment;
        this.date = date;
        this.user = user;
    }

    /**
     * get comment from the QRComment class
     * @return
     * a string of comment user made.
     */
    public String getComment() {
        return comment;
    }

    /**
     * get the date the comment made.
     * @return
     * a date of comment.
     */
    public String getDate() {
        return date;
    }

    /**
     * get the user who made comment.
     * @return
     * a string of the username.
     */
    public String getUser() {
        return user;
    }


}
