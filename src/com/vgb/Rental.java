package com.vgb;

public class Rental extends Item {
    private Equipment equipment;
    private double hours;

    public Rental(String uuid, Equipment equipment, double hours) {
        super(uuid, "Rental of " + equipment.getName());
        this.equipment = equipment;
        this.hours = hours;
    }

    /** Implements cost and tax for renting */
    @Override
    public double getCost() {
        return roundToCent(equipment.getRetailPrice() * 0.001 * hours);
    }

    @Override
    public double getTax() {
        return roundToCent(getCost() * 0.0438);
    }
}
