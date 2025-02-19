package com.vgb;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String uuid;
    private String firstName;
    private String lastName;
    private List<String> emails;

    public Person(String uuid, String firstName, String lastName, List<String> emails) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emails = (emails != null) ? new ArrayList<>(emails) : new ArrayList<>();
    }

	public String getUuid() {
		return uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public List<String> getEmails() {
		return emails;
	}
    
}
