package com.aputech.dora.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Comment {
    private String uid,commentText,time;
    private int priority;
    private String docId;
    private ArrayList<String> downvote;
    private ArrayList<String> upvote;
    public Comment() {
        //empty constructor needed
    }

    public Comment(String uid, String commentText, String time, int priority, String docId, ArrayList<String> downvote, ArrayList<String> upvote) {
        this.uid = uid;
        this.commentText = commentText;
        this.time = time;
        this.priority = priority;
        this.docId = docId;
        this.downvote = downvote;
        this.upvote = upvote;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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
