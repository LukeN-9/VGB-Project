package com.vgb;

import java.util.UUID;

public class Contract extends Item {
    private UUID companyUuid;

    public Contract(String uuid, String name, String companyUuid) {
        super(uuid, name);
        this.companyUuid = UUID.fromString(companyUuid);
    }

	public UUID getCompanyUuid() {
		return companyUuid;
	}
	
	@Override
	public String toString() {
	    return "Contract: " + getName() + " (Company UUID: " + companyUuid + ")";
	}

}
