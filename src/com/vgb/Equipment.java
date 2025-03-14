package com.vgb;

public class Equipment extends Item {
    private String modelNumber;
    private double retailPrice;

    public Equipment(String uuid, String name, String modelNumber, double retailPrice) {
        super(uuid, name);
        this.modelNumber = modelNumber;
        this.retailPrice = retailPrice;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    /** Implements cost and tax for purchased equipment */
    @Override
    public double getCost() {
        return retailPrice;
    }

    @Override
    public double getTax() {
        return roundToCent(retailPrice * 0.0525);
    }

}

