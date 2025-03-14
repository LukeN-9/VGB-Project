package com.vgb;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Person{
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String phone;
    private List<String> emails;

    public Person(String uuid, String firstName, String lastName, String phone, List<String> emails) {
        this.uuid = UUID.fromString(uuid);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.emails = (emails != null) ? new ArrayList<>(emails) : new ArrayList<>();
    }

	public UUID getUuid() {
		return uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhone() {
		return phone;
	}
	
	public List<String> getEmails() {
		return emails;
	}
	
	@Override
	public String toString() {
	    return lastName + ", " + firstName + " (" + uuid + ")\n" + emails;
	}

    
}
