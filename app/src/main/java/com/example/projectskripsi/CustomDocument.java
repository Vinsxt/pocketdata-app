package com.example.projectskripsi;

public class CustomDocument {

    private String imageUrl;
    private Document document;
    private String type;

    public CustomDocument() {
        // Required empty constructor for Firestore
    }

    public CustomDocument(
            String imageUrl,
            Document document
    ) {
        this.imageUrl = imageUrl;
        this.document = document;
        this.type = "CUSTOM";
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Document getDocument() {
        return document;
    }

    public String getType() {
        return type;
    }
}