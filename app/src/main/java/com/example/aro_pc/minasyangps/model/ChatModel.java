package com.example.aro_pc.minasyangps.model;

/**
 * Created by Aro-PC on 7/14/2017.
 */

public class ChatModel {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String message;
    private String userName;
}
