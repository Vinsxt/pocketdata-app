package com.example.projectskripsi;

public class DocumentItem {
    public String firestoreId;
    public String type;
    public Document document;

    public DocumentItem(String firestoreId, String type, Document document) {
        this.firestoreId = firestoreId;
        this.type = type;
        this.document = document;
    }
}
