package com.example.qrhunter;

import android.provider.Settings;

public class Profile {
    private final String userID;
    private String userName;
    private String fullName;
    private String email;
    private String phone;

    public Profile() {
        this.userID = Settings.Secure.ANDROID_ID;
    }

    public Profile(String userName, String fullName, String email, String phone) {
        this.userID = Settings.Secure.ANDROID_ID;

        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    /*
     * Method sets userName attribute
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /*
     * Method sets fullName attribute
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /*
     * Method sets email attribute
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /*
     * Method sets phone attribute
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /*
     * Method return userName attribute
     * @return
     * String
     */
    public String getUserName() {
        return userID;
    }

    /*
     * Method returns fullName attribute
     * @return
     * String
     */
    public String getFullName() {
        return fullName;
    }

    /*
     * Method returns email attribute
     * @return
     * String
     */
    public String getEmail() {
        return email;
    }

    /*
     * Method returns phone attribute
     * @return
     * String
     */
    public String getPhone() {
        return phone;
    }
}

