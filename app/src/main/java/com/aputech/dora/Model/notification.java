package com.aputech.dora.Model;

public class notification {
    private String Text,Document,userid,time;
    public notification() {
        //empty constructor needed
    }

    public notification(String text, String document, String userid, String time) {
        Text = text;
        Document = document;
        this.userid = userid;
        this.time = time;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
