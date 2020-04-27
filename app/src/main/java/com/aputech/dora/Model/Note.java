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
    private String downvoteref;
    private String upvoteref;
    private String refComments;
    private float priority;
    private String videoUrl;
    private String audioUrl;
    private int commentnum;
    private int upnum;
    private int downnum;

    public Note() {
        //empty constructor needed
    }

    public Note(String description, String imageUrl, int type, String userid, String uptime, GeoPoint location, String downvoteref, String upvoteref, String refComments, float priority, String videoUrl, String audioUrl, int commentnum, int upnum, int downnum) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.userid = userid;
        this.uptime = uptime;
        this.location = location;
        this.downvoteref = downvoteref;
        this.upvoteref = upvoteref;
        this.refComments = refComments;
        this.priority = priority;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
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
        downvoteref = in.readString();
        upvoteref = in.readString();
        refComments = in.readString();
        priority = in.readFloat();
        videoUrl = in.readString();
        audioUrl = in.readString();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getDownvoteref() {
        return downvoteref;
    }

    public void setDownvoteref(String downvoteref) {
        this.downvoteref = downvoteref;
    }

    public String getUpvoteref() {
        return upvoteref;
    }

    public void setUpvoteref(String upvoteref) {
        this.upvoteref = upvoteref;
    }

    public String getRefComments() {
        return refComments;
    }

    public void setRefComments(String refComments) {
        this.refComments = refComments;
    }

    public float getPriority() {
        return priority;
    }

    public void setPriority(float priority) {
        this.priority = priority;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

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
        dest.writeString(downvoteref);
        dest.writeString(upvoteref);
        dest.writeString(refComments);
        dest.writeFloat(priority);
        dest.writeString(videoUrl);
        dest.writeString(audioUrl);
        dest.writeInt(commentnum);
        dest.writeInt(upnum);
        dest.writeInt(downnum);
    }
}
