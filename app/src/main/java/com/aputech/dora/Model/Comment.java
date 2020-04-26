package com.aputech.dora.Model;

import com.google.firebase.firestore.CollectionReference;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Comment {
    private String uid,commentText,time;
    private int priority;
    private CollectionReference downvote;
    private CollectionReference upvote;

    public Comment() {
        //empty constructor needed
    }

    public Comment(String uid, String commentText, String time, int priority, CollectionReference downvote, CollectionReference upvote) {
        this.uid = uid;
        this.commentText = commentText;
        this.time = time;
        this.priority = priority;
        this.downvote = downvote;
        this.upvote = upvote;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public CollectionReference getDownvote() {
        return downvote;
    }

    public void setDownvote(CollectionReference downvote) {
        this.downvote = downvote;
    }

    public CollectionReference getUpvote() {
        return upvote;
    }

    public void setUpvote(CollectionReference upvote) {
        this.upvote = upvote;
    }
}
