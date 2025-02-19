package com.vgb;

public class Company {
    private String companyUuid;
    private String contactUuid;
    private String name;
    private String street;
    private String city;
    private String state;
    private String zip;

    public Company(String companyUuid, String contactUuid, String name, String street, String city, String state, String zip) {
        this.companyUuid = companyUuid;
        this.contactUuid = contactUuid;
        this.name = name;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

	public String getCompanyUuid() {
		return companyUuid;
	}

	public String getContactUuid() {
		return contactUuid;
	}

	public String getName() {
		return name;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZip() {
		return zip;
	}

}