package com.vgb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DataConverter {
    public static void main(String[] args) {
        String dataDir = "data";
        
        // Parse CSV files
        List<Person> persons = parsePersons(dataDir + "/Persons.csv");
        List<Company> companies = parseCompanies(dataDir + "/Companies.csv");
        List<Item> items = parseItems(dataDir + "/Items.csv");

        // Serialize to JSON
        serializeToJson(persons, dataDir + "/Persons.json");
        serializeToJson(companies, dataDir + "/Companies.json");
        serializeToJson(items, dataDir + "/Items.json");

        // Serialize to XML
        serializeToXml(persons, dataDir + "/Persons.xml");
        serializeToXml(companies, dataDir + "/Companies.xml");
        serializeToXml(items, dataDir + "/Items.xml");
    }

    private static List<Person> parsePersons(String filePath) {
        List<Person> persons = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            lines.remove(0);
            for (String line : lines) {
                String[] parts = line.split(",");
                String uuid = parts[0];
                String firstName = parts[1];
                String lastName = parts[2];
                List<String> emails = parts.length > 3 ? Arrays.asList(parts[3].split(";")) : new ArrayList<>();
                persons.add(new Person(uuid, firstName, lastName, emails));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return persons;
    }

    private static List<Company> parseCompanies(String filePath) {
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

    private static List<Item> parseItems(String filePath) {
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
                        items.add(new Material(uuid, name, parts[3], Double.parseDouble(parts[4])));
                        break;
                    case "C":
                        items.add(new Contract(uuid, name, parts[3]));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    private static void serializeToJson(Object data, String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
            System.out.println("JSON written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void serializeToXml(Object data, String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            XStream xstream = new XStream(new DomDriver());
            xstream.alias("data", List.class);
            xstream.toXML(data, writer);
            System.out.println("XML written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
