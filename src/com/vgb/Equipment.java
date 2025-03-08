package com.vgb;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

    public double equipmentTax(double cost) {
        return roundToCent(retailPrice * 0.0525);
    }

    /** Calculates leasing cost */
    public double calculateLeaseCost(LocalDate startDate, LocalDate endDate) {
        long leaseDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double amortizedLeaseTime = (double) leaseDays / 365 / 5;
        return roundToCent(amortizedLeaseTime * retailPrice * 1.5);
    }

    /** Calculates lease tax */
    public double calculateLeaseTax(LocalDate startDate, LocalDate endDate) {
        double leaseCost = calculateLeaseCost(startDate, endDate);
        return (leaseCost > 12500) ? 1500 : 0;
    }

    /** Calculates rental cost */
    public double calculateRentCost(double hours) {
        return roundToCent(retailPrice * 0.001 * hours);
    }

    /** Calculates rental tax */
    public double calculateRentTax(double rentCost) {
        return roundToCent(rentCost * 0.0438);
    }

    @Override
    public String toString() {
        return "Equipment: " + getName() + " (Model: " + modelNumber + ", Price: $" + String.format("%.2f", retailPrice) + ")";
    }

}
