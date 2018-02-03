package com.example.vanessa.p_etika.model;

/**
 * Created by Vanessa on 15/11/2017.
 */

public class Patient {

    String email;
    String phone;
    String password;

    public Patient(String email, String phone, String password) {
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public Patient (){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //implement Patient class in Google signIn and HomeActivity
}
