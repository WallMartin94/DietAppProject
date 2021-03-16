package com.example.dietappproject.dbobject;

import com.google.firebase.firestore.Exclude;

public class User {
    private String documentId;
    private String name;
    private String email;
    private String gender;
    private String dateOfBirth;
    private double calorieGoal;
    private double fatGoal;
    private double carbsGoal;
    private double proteinGoal;
    private double weight;
    private double height;


    public User() {
        //Public empty constructor for Firestore
    }

    public User(String name, String email, String gender, String dateOfBirth,
                double calorieGoal, double fatGoal, double carbsGoal, double proteinGoal,
                double weight, double height) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.calorieGoal = calorieGoal;
        this.fatGoal = fatGoal;
        this.carbsGoal = carbsGoal;
        this.proteinGoal = proteinGoal;
        this.weight = weight;
        this.height = height;
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

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

}
