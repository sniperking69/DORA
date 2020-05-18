package com.aputech.dora.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Comment {
    @ServerTimestamp
    private Date timestamp;
    private String uid, commentText;
    private int priority;
    private int upnum;
    private int downnum;
    private String commentid;


    public Comment() {
        //empty constructor needed
    }

    public Comment(Date timestamp, String uid, String commentText, int priority, int upnum, int downnum, String commentid) {
        this.timestamp = timestamp;
        this.uid = uid;
        this.commentText = commentText;
        this.priority = priority;
        this.upnum = upnum;
        this.downnum = downnum;
        this.commentid = commentid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }
}
