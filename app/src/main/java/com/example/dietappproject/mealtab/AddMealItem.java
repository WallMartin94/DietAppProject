package com.example.dietappproject.mealtab;

public class AddMealItem {
    private String documentId;
    private String name;
    private String barcodeId;
    private double fat, carbs, protein, calories, amount;

    public AddMealItem (String documentId, String name, String barcodeId,
                        double fat, double carbs, double protein, double calories, double amount) {
        this.documentId = documentId;
        this.name = name;
        this.barcodeId = barcodeId;
        this.fat = fat;
        this.carbs = carbs;
        this.protein = protein;
        this.calories = calories;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getBarcodeId() {
        return barcodeId;
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

    public double getCalories() {
        return calories;
    }

    public double getAmount() {
        return amount;
    }

    public String getDocumentId() {
        return documentId;
    }
}
