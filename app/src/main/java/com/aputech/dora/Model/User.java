package com.aputech.dora.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.aputech.dora.ui.Post;

import java.util.ArrayList;

public class User implements Parcelable {

    private String UserName;
    private int postnum;
    private String Bio;
    private String EmailAdress;
    private String Gender;
    private ArrayList<String> followers;
    private ArrayList<String> following;
    private ArrayList<String> posts;
    private int Userlevel;
    public User() {
        //empty constructor needed
    }

    public User(String userName, String bio, String emailAdress, String gender, ArrayList<String> followers, ArrayList<String> following, ArrayList<String> posts, int userlevel, String userid, String profileUrl, String facebookId, String instaId) {
        UserName = userName;
        Bio = bio;
        EmailAdress = emailAdress;
        Gender = gender;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        Userlevel = userlevel;
        this.userid = userid;
        ProfileUrl = profileUrl;
        this.facebookId = facebookId;
        this.instaId = instaId;
    }

    private String userid;
    private String ProfileUrl; //Backend Url of Image
    //Optional
    private String facebookId;
    private String instaId;

    protected User(Parcel in) {
        UserName = in.readString();
        Bio = in.readString();
        EmailAdress = in.readString();
        Gender = in.readString();
        followers = in.createStringArrayList();
        following = in.createStringArrayList();
        posts = in.createStringArrayList();
        Userlevel = in.readInt();
        userid = in.readString();
        ProfileUrl = in.readString();
        facebookId = in.readString();
        instaId = in.readString();
    }

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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getProfileUrl() {
        return ProfileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        ProfileUrl = profileUrl;
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

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getInstaId() {
        return instaId;
    }

    public void setInstaId(String instaId) {
        this.instaId = instaId;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<String> posts) {
        this.posts = posts;
    }

    public int getUserlevel() {
        return Userlevel;
    }

    public void setUserlevel(int userlevel) {
        Userlevel = userlevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(UserName);
        dest.writeString(Bio);
        dest.writeString(EmailAdress);
        dest.writeString(Gender);
        dest.writeStringList(followers);
        dest.writeStringList(following);
        dest.writeStringList(posts);
        dest.writeInt(Userlevel);
        dest.writeString(userid);
        dest.writeString(ProfileUrl);
        dest.writeString(facebookId);
        dest.writeString(instaId);
    }
}
