package com.aputech.dora.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//ups and downs are actually a list
public class Note implements Parcelable {
    private String description;
    private String imageUrl;
    private int type;
    private String userid;
    private String uptime;
    private GeoPoint location;
    private ArrayList<String> downvote;
    private ArrayList<String> upvote;
    private DocumentReference refComments;
    private float priority;
    private int commentnum;
    private int upnum;
    private int downnum;

    public Note() {
        //empty constructor needed
    }

    public Note(String description, String imageUrl, int type, String userid, String uptime, GeoPoint location, ArrayList<String> downvote, ArrayList<String> upvote, DocumentReference refComments, float priority, int commentnum, int upnum, int downnum) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.userid = userid;
        this.uptime = uptime;
        this.location = location;
        this.downvote = downvote;
        this.upvote = upvote;
        this.refComments = refComments;
        this.priority = priority;
        this.commentnum = commentnum;
        this.upnum = upnum;
        this.downnum = downnum;
    }

    protected Note(Parcel in) {
        description = in.readString();
        imageUrl = in.readString();
        type = in.readInt();
        userid = in.readString();
        uptime = in.readString();
        downvote = in.createStringArrayList();
        upvote = in.createStringArrayList();
        priority = in.readFloat();
        commentnum = in.readInt();
        upnum = in.readInt();
        downnum = in.readInt();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
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

    public DocumentReference getRefComments() {
        return refComments;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
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

    public float getPriority() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeInt(type);
        dest.writeString(userid);
        dest.writeString(uptime);
        dest.writeStringList(downvote);
        dest.writeStringList(upvote);
        dest.writeFloat(priority);
        dest.writeInt(commentnum);
        dest.writeInt(upnum);
        dest.writeInt(downnum);
    }
}
