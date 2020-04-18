package com.aputech.dora.Model;

import android.location.Location;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//ups and downs are actually a list
public class Note {
    private String description;
    private String imageUrl;
    private int type;
    private String userid;
    private String uptime;
    private Location location;
    private ArrayList<String> downvote;
    private ArrayList<String> upvote;
    private DocumentReference refComments;
    private int priority;

    public Note() {
        //empty constructor needed
    }

    public Note(String description, int priority, String imageUrl, int type,Location location,String uptime,String userid, ArrayList<String> upvote,ArrayList<String> downvote,DocumentReference refComments) {

        this.description = description;
        this.priority = priority;
        this.imageUrl=imageUrl;
        this.type=type;
        this.refComments=refComments;
        this.uptime=uptime;
        this.downvote=downvote;
        this.upvote=upvote;
        this.uptime=uptime;
        this.userid=userid;
        this.location=location;

    }

    public DocumentReference getRefComments() {
        return refComments;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setRefComments(DocumentReference refComments) {
        this.refComments = refComments;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public ArrayList<String> getDownvote() {
        return downvote;
    }

    public void setDownvote(ArrayList<String> downvote) {
        this.downvote = downvote;
    }

    public ArrayList<String> getUpvote() {
        return upvote;
    }

    public void setUpvote(ArrayList<String> upvote) {
        this.upvote = upvote;
    }

    public int getType() {
        return type;
    }
}
