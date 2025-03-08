package com.vgb;

public class Material extends Item {
    private String unit;
    private double costPerUnit;

    public Material(String uuid, String name, String unit, double costPerUnit) {
        super(uuid, name);
        this.unit = unit;
        this.costPerUnit = costPerUnit;
    }

    public String getUnit() {
        return unit;
    }

    public double getCostPerUnit() {
        return costPerUnit;
    }

    /** Calculates the cost for purchasing materials */
    public double calculateMaterialCost(int units) {
        return roundToCent(costPerUnit * units);
    }

    /** Calculates tax on material purchase */
    public double calculateMaterialTax(int units) {
        return roundToCent(calculateMaterialCost(units) * 0.0715);
    }

    @Override
    public String toString() {
        return "Material: " + getName() + " (Unit: " + unit + ", Cost per Unit: $" + String.format("%.2f", costPerUnit) + ")";
    }
}

