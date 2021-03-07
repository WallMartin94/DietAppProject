package com.example.dietappproject.dbobject;

import com.google.firebase.firestore.Exclude;

import java.util.Date;
import java.util.Map;

public class Meal {
    private String documentId;
    private String category;
    private Date date;
    private String mealUser;
    private double fat, carbs, protein, calories;
    Map<String, Double> mealItems;

    public Meal() {
        //Public empty constructor for Firestore
    }

    public Meal(String category, String mealUser, Date date,
                double fat, double carbs, double protein, double calories,
                Map<String, Double> mealItems) {
        this.category = category;
        this.mealUser = mealUser;
        this.date = date;
        this.fat = fat;
        this.carbs = carbs;
        this.protein = protein;
        this.calories = calories;
        this.mealItems = mealItems;

    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public String getMealUser() {
        return mealUser;
    }

    public Map<String, Double> getMealItems() {
        return mealItems;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public double getCalories() {
        return calories;
    }

    public double getFat() {
        return fat;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getProtein() {
        return protein;
    }
}
