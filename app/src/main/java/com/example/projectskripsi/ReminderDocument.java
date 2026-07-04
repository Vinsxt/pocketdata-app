package com.example.projectskripsi;

import java.util.Date;
import java.util.List;

public class ReminderDocument {

    private Date reminderDate;
    private String imageUrl;
    private Document document;
    private String type;
    private int[] reminderOffsets;
    private List<Integer> reminderRequestCodes;

    public ReminderDocument() {
        // Required empty constructor for Firestore
    }

    public ReminderDocument(
            Date reminderDate,
            String imageUrl,
            Document document
    ) {
        this.reminderDate = reminderDate;
        this.imageUrl = imageUrl;
        this.document = document;
        this.type = "REMINDER";
    }

    public Date getReminderDate() {
        return reminderDate;
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

    public int[] getReminderOffsets() {
        return reminderOffsets;
    }

    public void setReminderOffsets(int[] reminderOffsets) {
        this.reminderOffsets = reminderOffsets;
    }

    public List<Integer> getReminderRequestCodes() {
        return reminderRequestCodes;
    }

    public void setReminderRequestCodes(List<Integer> reminderRequestCodes) {
        this.reminderRequestCodes = reminderRequestCodes;
    }
}