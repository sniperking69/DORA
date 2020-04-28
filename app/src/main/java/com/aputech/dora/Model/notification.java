package com.aputech.dora.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class notification {
    @ServerTimestamp
    private Date timestamp;
    private String Text,Document,userid;
    public notification() {
        //empty constructor needed
    }

    public notification(Date timestamp, String text, String document, String userid) {
        this.timestamp = timestamp;
        Text = text;
        Document = document;
        this.userid = userid;

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getDocument() {
        return Document;
    }

    public void setDocument(String document) {
        Document = document;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
