package com.vgb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Company {
    private UUID companyUuid;
    private Person contact;
    private String name;
    private Address address;

    public Company(String companyUuid, Person contact, String name, Address address) {
        this.companyUuid = UUID.fromString(companyUuid);
        this.contact = contact;
        this.name = name;
        this.address = address;
    }

    public UUID getCompanyUuid() {
        return companyUuid;
    }

    public Person getContact() {
        return contact;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public static List<Company> parseCompanies(String filePath, List<Person> persons) {
        List<Company> companies = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            lines.remove(0);
            for (String line : lines) {
                String[] parts = line.split(",");
                UUID contactUuid = UUID.fromString(parts[1]);
                Person contact = persons.stream()
                        .filter(person -> person.getUuid().equals(contactUuid))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Person not found for contactUuid"));

                Address address = new Address(parts[3], parts[4], parts[5], parts[6]); // Assuming address is part of CSV
                companies.add(new Company(parts[0], contact, parts[2], address));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return companies;
    }
    
    @Override
    public String toString() {
        return "Company: " + name + "\nAddress: " + address.getStreet() + ", " + address.getCity() + 
               ", " + address.getState() + " " + address.getZip() + "\nContact: " + contact.toString();
    }

}
