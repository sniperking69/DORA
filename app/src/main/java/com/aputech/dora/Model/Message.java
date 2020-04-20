package com.aputech.dora.Model;

import android.location.Location;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class Message {
    private String description;
    private String imageUrl;
    private int type;
    private String userid;
    private String uptime;
    private Location location;
    public Message() {
        //empty constructor needed
    }
    public Message(String description, String imageUrl, int type, String userid, String uptime, Location location) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.userid = userid;
        this.uptime = uptime;
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
