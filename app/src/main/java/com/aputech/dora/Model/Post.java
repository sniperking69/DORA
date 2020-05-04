package com.aputech.dora.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;


import java.util.Date;

//ups and downs are actually a list
public class Post implements Parcelable {
    @ServerTimestamp
    private Date timestamp;
    private String description;
    private String imageUrl;
    private int type;
    private String userid;
    private GeoPoint location;
    private String refComments;
    private float priority;
    private String videoUrl;
    private String audioUrl;
    private int commentnum;
    private int upnum;
    private int downnum;

    public Post() {
        //empty constructor needed
    }

    public Post(Date timestamp, String description, String imageUrl, int type, String userid, GeoPoint location, String refComments, float priority, String videoUrl, String audioUrl, int commentnum, int upnum, int downnum) {
        this.timestamp = timestamp;
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.userid = userid;
        this.location = location;
        this.refComments = refComments;
        this.priority = priority;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
        this.commentnum = commentnum;
        this.upnum = upnum;
        this.downnum = downnum;
    }

    protected Post(Parcel in) {
        description = in.readString();
        imageUrl = in.readString();
        type = in.readInt();
        userid = in.readString();
        refComments = in.readString();
        priority = in.readFloat();
        videoUrl = in.readString();
        audioUrl = in.readString();
        commentnum = in.readInt();
        upnum = in.readInt();
        downnum = in.readInt();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

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

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
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
        dest.writeString(refComments);
        dest.writeFloat(priority);
        dest.writeString(videoUrl);
        dest.writeString(audioUrl);
        dest.writeInt(commentnum);
        dest.writeInt(upnum);
        dest.writeInt(downnum);
    }
}
