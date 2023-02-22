package com.example.qrhunter;

import android.util.Log;

import java.util.Observable;

public class UserProfile {
    private String userName;
    private String fullName;
    private String email;
    private String phone;


    public UserProfile() {

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
        return userName;
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

