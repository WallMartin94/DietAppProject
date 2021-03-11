package com.example.dietappproject.mealtab;

public class MealDetailsPieData {
    String macroType;
    double amount;

    public MealDetailsPieData(String macroType, double amount) {
        this.macroType = macroType;
        this.amount = amount;
    }

    public String getMacroType() {
        return macroType;
    }

    public double getAmount() {
        return amount;
    }

    public void setMacroType(String macroType) {
        this.macroType = macroType;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
