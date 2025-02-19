package com.vgb;

public class Contract extends Item {
    private String companyUuid;

    public Contract(String uuid, String name, String companyUuid) {
        super(uuid, name);
        this.companyUuid = companyUuid;
    }

	public String getCompanyUuid() {
		return companyUuid;
	}

}
