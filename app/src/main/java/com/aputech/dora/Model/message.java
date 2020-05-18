package com.aputech.dora.Model;


import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class message {
    @ServerTimestamp
    private Date timestamp;
    private String description;
    private String imageUrl;
    private int type;
    private String refmsg;
    private GeoPoint location;
    private String videoUrl;
    private String audioUrl;
    private String sender;
    private String receiver;

    public message() {
        //empty constructor needed
    }

    public message(Date timestamp, String description, String imageUrl, int type, String refmsg, GeoPoint location, String videoUrl, String audioUrl, String sender, String receiver) {
        this.timestamp = timestamp;
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.refmsg = refmsg;
        this.location = location;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public String getRefmsg() {
        return refmsg;
    }

    public void setRefmsg(String refmsg) {
        this.refmsg = refmsg;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
