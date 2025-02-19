package com.vgb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Person {
    private String uuid;
    private String firstName;
    private String lastName;
    private String phone;
    private List<String> emails;

    public Person(String uuid, String firstName, String lastName, String phone, List<String> emails) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
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

	public String getPhone() {
		return phone;
	}
	
	public List<String> getEmails() {
		return emails;
	}
	
	public static List<Person> parsePersons(String filePath) {
        List<Person> persons = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            lines.remove(0);
            for (String line : lines) {
                String[] parts = line.split(",");
                String uuid = parts[0];
                String firstName = parts[1];
                String lastName = parts[2];
                String phone = parts[3];
                List<String> emails = parts.length > 4 ? Arrays.asList(parts[4].split(";")) : new ArrayList<>();
                persons.add(new Person(uuid, firstName, lastName, phone, emails));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return persons;
    }
    
}
