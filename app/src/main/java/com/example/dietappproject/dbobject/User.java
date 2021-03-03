package com.example.dietappproject.dbobject;

import com.google.firebase.firestore.Exclude;

public class User {
    private String documentId;
    private String name;

    public User() {
        //Public empty constructor for Firestore
    }

    public User(String documentId, String name) {
        this.documentId = documentId;
        this.name = name;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public String getName() {
        return name;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
