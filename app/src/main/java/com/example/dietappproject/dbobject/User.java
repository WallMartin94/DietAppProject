package com.example.dietappproject.dbobject;

import com.google.firebase.firestore.Exclude;

public class User {
    private String documentId;
    private String name;
    private double calorieGoal;
    private double fatGoal;
    private double carbsGoal;
    private double proteinGoal;

    public User() {
        //Public empty constructor for Firestore
    }

    public User(String name, double calorieGoal, double fatGoal, double carbsGoal, double proteinGoal) {
        this.name = name;
        this.calorieGoal = calorieGoal;
        this.fatGoal = fatGoal;
        this.carbsGoal = carbsGoal;
        this.proteinGoal = proteinGoal;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public String getName() {
        return name;
    }

    public double getCalorieGoal() {
        return calorieGoal;
    }

    public double getFatGoal() {
        return fatGoal;
    }

    public double getCarbsGoal() {
        return carbsGoal;
    }

    public double getProteinGoal() {
        return proteinGoal;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

}
