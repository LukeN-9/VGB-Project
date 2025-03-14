package com.vgb;

public class Material extends Item {
    private String unit;
    private double costPerUnit;
    private int quantity;

    public Material(String uuid, String name, String unit, double costPerUnit, int quantity) {
        super(uuid, name);
        this.unit = unit;
        this.costPerUnit = costPerUnit;
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public double getCostPerUnit() {
        return costPerUnit;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
    	this.quantity = quantity;
    }

    /** Implements cost and tax for materials */
    @Override
    public double getCost() {
        return roundToCent(costPerUnit * quantity);
    }

    @Override
    public double getTax() {
        return roundToCent(getCost() * 0.0715); // 7.15% tax
    }

}


