package com.vgb;

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

    public UUID getUuid() {
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
    
    @Override
    public String toString() {
        return String.format("%-32s (%s)\n%s\n%s",
                name, companyUuid, contact.toString(), address.toString());
    }


}
