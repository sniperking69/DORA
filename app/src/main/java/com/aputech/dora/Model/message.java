package com.aputech.dora.Model;


import com.google.firebase.firestore.GeoPoint;

public class message {
    private String description;
    private String imageUrl;
    private int type;
    private String sentBy;
    private String sentTo;
    private String uptime;
    private GeoPoint location;
    public message() {
        //empty constructor needed
    }

    public message(String description, String imageUrl, int type, String sentBy, String sentTo, String uptime, GeoPoint location) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.sentBy = sentBy;
        this.sentTo = sentTo;
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

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getSentTo() {
        return sentTo;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
