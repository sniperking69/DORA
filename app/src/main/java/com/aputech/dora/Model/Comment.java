package com.aputech.dora.Model;

public class Comment {
    private String uid,commentText,time;
    private int up,down,priority;
    public Comment() {
        //empty constructor needed
    }

    public Comment(String uid, String commentText, String time, int up, int down, int priority) {
        this.uid = uid;
        this.commentText = commentText;
        this.time = time;
        this.up = up;
        this.down = down;
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

    public int getUp() {
        return up;
    }

    public int getDown() {
        return down;
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

    public void setUp(int up) {
        this.up = up;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
