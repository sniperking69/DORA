package com.aputech.dora.Model;

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
    private List<String> downvote;
    private List<String> upvote;
    private DocumentReference refComments;
    private int priority;

    public Note() {
        //empty constructor needed
    }

    public Note(String description, int priority, String imageUrl, int type,String uptime,String userid, List<String> upvote,List<String> downvote,DocumentReference refComments) {

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

    public List<String> getDownvote() {
        return downvote;
    }

    public void setDownvote(List<String> downvote) {
        this.downvote = downvote;
    }

    public List<String> getUpvote() {
        return upvote;
    }

    public void setUpvote(List<String> upvote) {
        this.upvote = upvote;
    }

    public int getType() {
        return type;
    }
}
