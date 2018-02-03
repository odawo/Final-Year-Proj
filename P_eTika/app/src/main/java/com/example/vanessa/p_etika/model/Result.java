package com.example.vanessa.p_etika.model;

/**
 * Created by Vanessa on 20/11/2017.
 */

public class Result {

    public String message_id;

    public Result() {

    }

    public Result(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
