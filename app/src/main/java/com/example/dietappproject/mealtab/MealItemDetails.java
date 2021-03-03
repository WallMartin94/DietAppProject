package com.example.dietappproject.mealtab;

public class MealItemDetails {
    private String mName;
    private double mProtein;
    private double mFat;
    private double mCarbs;
    private double mCalories;

    public MealItemDetails(String name, double protein, double fat,
                           double carbs, double calories) {
        mName = name;
        mProtein = protein;
        mFat = fat;
        mCarbs = carbs;
        mCalories = calories;
    }

    public String getName() {
        return mName;
    }

    public double getProtein() {
        return mProtein;
    }

    public double getFat() {
        return mFat;
    }

    public double getCarbs() {
        return mCarbs;
    }

    public double getCalories() {
        return mCalories;
    }
}
