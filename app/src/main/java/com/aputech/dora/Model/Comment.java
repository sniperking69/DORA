package com.aputech.dora.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Comment {
    private String uid,commentText,time;
    private int priority;
    private ArrayList<String> downvote;
    private ArrayList<String> upvote;
    public Comment() {
        //empty constructor needed
    }

    public Comment(String uid, String commentText, String time, ArrayList<String> up,ArrayList<String> down, int priority) {
        this.uid = uid;
        this.commentText = commentText;
        this.time = time;
        this.upvote = up;
        this.downvote = down;
        this.priority = priority;
    }

    public String getUid() {
        return uid;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getTime() {
        return time;
    }


    public int getPriority() {
        return priority;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public void setTime(String time) {
        this.time = time;
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

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
