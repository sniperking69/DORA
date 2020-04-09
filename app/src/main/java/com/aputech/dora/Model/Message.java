package com.aputech.dora.Model;

public class Message {
    private String title;
    private String description;
    private int priority;

    public Message() {
        //empty constructor needed
    }

    public Message(String title, String description, int priority) {
        this.title = title;
        this.description = description;
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
}
