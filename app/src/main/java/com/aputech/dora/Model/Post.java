package com.aputech.dora.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;


import java.util.Date;

//ups and downs are actually a list
public class Post  {
    @ServerTimestamp
    private Date timestamp;

    private String description;
    private String imageUrl;
    private int type;
    private String userid;
    private String refComments;
    private float priority;
    private String videoUrl;
    private String audioUrl;
    private int commentnum;
    private int upnum;
    private int downnum;
    private GeoPoint location;

    public Post() {
        //empty constructor needed
    }

    public Post(Date timestamp, String description, String imageUrl, int type, String userid, String refComments, float priority, String videoUrl, String audioUrl, int commentnum, int upnum, int downnum, GeoPoint location) {
        this.timestamp = timestamp;
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.userid = userid;
        this.refComments = refComments;
        this.priority = priority;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
        this.commentnum = commentnum;
        this.upnum = upnum;
        this.downnum = downnum;
        this.location = location;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRefComments() {
        return refComments;
    }

    public void setRefComments(String refComments) {
        this.refComments = refComments;
    }

    public float getPriority() {
        return priority;
    }

    public void setPriority(float priority) {
        this.priority = priority;
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

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }

    public int getUpnum() {
        return upnum;
    }

    public void setUpnum(int upnum) {
        this.upnum = upnum;
    }

    public int getDownnum() {
        return downnum;
    }

    public void setDownnum(int downnum) {
        this.downnum = downnum;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
