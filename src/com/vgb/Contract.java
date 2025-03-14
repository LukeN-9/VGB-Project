package com.vgb;

import java.util.UUID;

public class Contract extends Item {
    private UUID companyUuid;
    private double cost;

    public Contract(String uuid, String name, String companyUuid, double cost) {
        super(uuid, name);
        this.companyUuid = UUID.fromString(companyUuid);
        this.cost = cost;
    }

	public UUID getCompanyUuid() {
		return companyUuid;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	/** Implements cost and tax for contract */
    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public double getTax() {
        return 0;
    }

}
