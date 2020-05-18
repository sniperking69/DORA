package com.aputech.dora.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String UserName;
    private int postnum;
    private String Bio;
    private String EmailAdress;
    private int Following;
    private int Follower;
    private String userid;
    private String ProfileUrl;

    public User() {
        //empty constructor needed
    }

    public User(String userName, int postnum, String bio, String emailAdress, int following, int follower, String userid, String profileUrl) {
        UserName = userName;
        this.postnum = postnum;
        Bio = bio;
        EmailAdress = emailAdress;
        Following = following;
        Follower = follower;
        this.userid = userid;
        ProfileUrl = profileUrl;
    }

    protected User(Parcel in) {
        UserName = in.readString();
        postnum = in.readInt();
        Bio = in.readString();
        EmailAdress = in.readString();
        Following = in.readInt();
        Follower = in.readInt();
        userid = in.readString();
        ProfileUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UserName);
        dest.writeInt(postnum);
        dest.writeString(Bio);
        dest.writeString(EmailAdress);
        dest.writeInt(Following);
        dest.writeInt(Follower);
        dest.writeString(userid);
        dest.writeString(ProfileUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public int getPostnum() {
        return postnum;
    }

    public void setPostnum(int postnum) {
        this.postnum = postnum;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getEmailAdress() {
        return EmailAdress;
    }

    public void setEmailAdress(String emailAdress) {
        EmailAdress = emailAdress;
    }

    public int getFollowing() {
        return Following;
    }

    public void setFollowing(int following) {
        Following = following;
    }

    public int getFollower() {
        return Follower;
    }

    public void setFollower(int follower) {
        Follower = follower;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProfileUrl() {
        return ProfileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        ProfileUrl = profileUrl;
    }
}