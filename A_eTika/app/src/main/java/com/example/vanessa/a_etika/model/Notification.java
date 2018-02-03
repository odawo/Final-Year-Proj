package com.example.vanessa.a_etika.model;

/**
 * Created by Vanessa on 20/11/2017.
 */

public class Notification {

    public String title;
    public String body; //detail

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    //get and set methods

}
/**
 * firebase database rules
 * {"rules":{
 *     ".read":"true",
 *     ".write":"true",
 *     "AMBULANCEDRIVERS":{
 *         ".indexOn":["g"]
 *     }
 * }}
 * */