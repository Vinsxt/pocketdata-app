package com.example.projectskripsi;

public class Document {
    private String title;
    private String description;
    private String date;
    private String imageUrl;

    public Document() {}

    // Constructor
    public Document(String title, String description, String date, String imageUrl) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
