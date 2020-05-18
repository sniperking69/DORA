package com.aputech.dora.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class notification {
    @ServerTimestamp
    private Date timestamp;
    private int typ;
    private String Text, Document, userid;

    public notification() {
        //empty constructor needed
    }

    public notification(Date timestamp, int typ, String text, String document, String userid) {
        this.timestamp = timestamp;
        this.typ = typ;
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

    public int getTyp() {
        return typ;
    }

    public void setTyp(int typ) {
        this.typ = typ;
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
