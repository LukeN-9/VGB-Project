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

}
