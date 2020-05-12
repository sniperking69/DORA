package com.aputech.dora.Model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class message implements Parcelable {
    @ServerTimestamp
    private Date timestamp;


    private String description;
    private String imageUrl;
    private int type;
    private String userid;
    private GeoPoint location;
    private String videoUrl;
    private String audioUrl;
    public message() {
        //empty constructor needed
    }

    public message(Date timestamp, String description, String imageUrl, int type, String userid, GeoPoint location, String videoUrl, String audioUrl) {
        this.timestamp = timestamp;
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.userid = userid;
        this.location = location;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
    }

    protected message(Parcel in) {
        description = in.readString();
        imageUrl = in.readString();
        type = in.readInt();
        userid = in.readString();
        videoUrl = in.readString();
        audioUrl = in.readString();
    }

    public static final Creator<message> CREATOR = new Creator<message>() {
        @Override
        public message createFromParcel(Parcel in) {
            return new message(in);
        }

        @Override
        public message[] newArray(int size) {
            return new message[size];
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
        dest.writeString(videoUrl);
        dest.writeString(audioUrl);
    }
}
