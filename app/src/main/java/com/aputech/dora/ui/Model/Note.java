package com.aputech.dora.ui.Model;

public class Note {
    private String title;
    private String description;
    private String imageUrl;
    private int type;
    private int priority;

    public Note() {
        //empty constructor needed
    }

    public Note(String title, String description, int priority,String imageUrl,int type) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.imageUrl=imageUrl;
        this.type=type;
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
