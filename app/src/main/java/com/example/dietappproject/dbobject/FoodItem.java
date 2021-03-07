package com.example.dietappproject.dbobject;

import com.google.firebase.firestore.Exclude;

public class FoodItem {
    private String documentId;
    private String barcodeId;
    private String name;
    private String type;
    private double protein;
    private double fat;
    private double carbs;
    private double calories;

    public FoodItem() {
        //Public empty constructor for Firestore
    }

    public FoodItem(String barcodeId, String name, String type,
                    double protein, double fat, double carbs,
                    double calories) {
        this.barcodeId = barcodeId;
        this.name = name;
        this.type = type;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.calories = calories;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getProtein() {
        return protein;
    }

    public double getFat() {
        return fat;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getCalories() {
        return calories;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}


