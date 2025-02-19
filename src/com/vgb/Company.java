package com.vgb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
	
	public static List<Company> parseCompanies(String filePath) {
        List<Company> companies = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            lines.remove(0);
            for (String line : lines) {
                String[] parts = line.split(",");
                companies.add(new Company(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return companies;
    }

}