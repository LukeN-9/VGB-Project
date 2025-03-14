package com.vgb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class ParseData {

    public static List<Item> parseItems(String filePath) {
        List<Item> items = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            lines.remove(0);
            for (String line : lines) {
                String[] parts = line.split(",");
                String uuid = parts[0];
                String type = parts[1];
                String name = parts[2];
                switch (type) {
                    case "E":
                        items.add(new Equipment(uuid, name, parts[3], Double.parseDouble(parts[4])));
                        break;
                    case "M":
                        items.add(new Material(uuid, name, parts[3], Double.parseDouble(parts[4]), 0));
                        break;
                    case "C":
                        items.add(new Contract(uuid, name, parts[3], 0));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
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

                Address address = new Address(parts[3], parts[4], parts[5], parts[6]);
                companies.add(new Company(parts[0], contact, parts[2], address));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return companies;
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

    /** Parses invoices from CSV */
    public static List<Invoice> parseInvoices(String filePath, List<Company> companies, List<Person> persons) {
        List<Invoice> invoices = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            lines.remove(0);

            for (String line : lines) {
                String[] data = line.split(",");
                UUID invoiceUUID = UUID.fromString(data[0]);
                UUID customerUUID = UUID.fromString(data[1]);
                UUID salesPersonUUID = UUID.fromString(data[2]);
                LocalDate date = LocalDate.parse(data[3]);

                Company customer = companies.stream()
                        .filter(c -> c.getUuid().equals(customerUUID))
                        .findFirst().orElse(null);

                Person salesPerson = persons.stream()
                        .filter(p -> p.getUuid().equals(salesPersonUUID))
                        .findFirst().orElse(null);

                Invoice invoice = new Invoice(invoiceUUID.toString(), date, customer, salesPerson, new ArrayList<>());
                invoices.add(invoice);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return invoices;
    }



    /** Parses invoice items and associates them with invoices */
    public static void parseInvoiceItems(String filePath, List<Invoice> invoices, List<Item> items) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            lines.remove(0);

            for (String line : lines) {
                String[] data = line.split(",");
                UUID invoiceUUID = UUID.fromString(data[0]);
                UUID itemUUID = UUID.fromString(data[1]);

                Invoice invoice = invoices.stream()
                        .filter(inv -> inv.getInvoiceUuid().equals(invoiceUUID))
                        .findFirst().orElse(null);

                Item baseItem = items.stream()
                        .filter(i -> i.getUuid().equals(itemUUID.toString()))
                        .findFirst().orElse(null);
                if (invoice != null && baseItem != null) {
                    if (baseItem instanceof Contract) {
                        double contractCost = Double.parseDouble(data[2]);
                        ((Contract) baseItem).setCost(contractCost);
                        invoice.addItem(baseItem);
                    }
                    else if(baseItem instanceof Material) {
                        int materialQuantity = Integer.parseInt(data[2]);
                        ((Material) baseItem).setQuantity(materialQuantity);
                        invoice.addItem(baseItem);
                    }
                    else if(data.length == 5) {
                    	Lease item = new Lease(baseItem.getUuid().toString(), (Equipment)baseItem, data[3], data[4]);
                    	invoice.addItem(item);
                    }
                    else if(data.length == 4) {
                    	Rental item = new Rental(baseItem.getUuid().toString(), (Equipment)baseItem, Double.parseDouble(data[3]));
                    	invoice.addItem(item);
                    }
                    else {
                    	invoice.addItem(baseItem);
                    }

                    
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

