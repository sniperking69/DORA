package com.aputech.dora.Model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

//ups and downs are actually a list
public class Note {
    private String title;
    private String description;
    private String imageUrl;
    private int type;
    private DocumentReference refComments;
    private int priority;

    public Note() {
        //empty constructor needed
    }

    public Note(String title, String description, int priority, String imageUrl, int type, DocumentReference refComments) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.imageUrl=imageUrl;
        this.type=type;
        this.refComments=refComments;

    }

    public DocumentReference getrefComments() {
        return refComments;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getType() {
        return type;
    }
}
