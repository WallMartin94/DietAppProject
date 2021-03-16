package com.example.dietappproject.statstab;

public class StatsBarChartCategoryData {
    String category;
    double amount;

    public StatsBarChartCategoryData(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public void setMacroType(String macroType) {
        this.category = macroType;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
