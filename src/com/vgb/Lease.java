package com.vgb;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Lease extends Item {
    private Equipment equipment;
    private LocalDate startDate;
    private LocalDate endDate;

    public Lease(String uuid, Equipment equipment, String startDate, String endDate) {
        super(uuid, "Lease of " + equipment.getName());
        this.equipment = equipment;
        this.startDate = LocalDate.parse(startDate);
        this.endDate = LocalDate.parse(endDate);
    }

    /** Implements cost and tax for leasing */
    @Override
    public double getCost() {
        long leaseDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double leaseYears = (double) leaseDays / 365.0; // Convert days to years
        double amortizationFactor = leaseYears / 5.0; // Spread cost over 5 years dynamically
        double cost = amortizationFactor * equipment.getRetailPrice() * 1.5; // Apply markup
        return roundToCent(cost);
    }

    @Override
    public double getTax() {
        return (getCost() > 12500) ? 1500 : 0;
    }
}

