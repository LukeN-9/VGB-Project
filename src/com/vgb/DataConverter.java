package com.vgb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.util.*;

public class DataConverter {
    public static void main(String[] args) {
        String dataDir = "data";
        
        // Parse CSV files
        List<Person> persons = Person.parsePersons(dataDir + "/Persons.csv");
        List<Company> companies = Company.parseCompanies(dataDir + "/Companies.csv", persons); // Pass persons to Company parsing
        List<Item> items = Item.parseItems(dataDir + "/Items.csv");

        // Serialize to JSON
        serializeToJson(persons, dataDir + "/Persons.json");
        serializeToJson(companies, dataDir + "/Companies.json");
        serializeToJson(items, dataDir + "/Items.json");

        // Serialize to XML
        serializeToXml(persons, dataDir + "/Persons.xml");
        serializeToXml(companies, dataDir + "/Companies.xml");
        serializeToXml(items, dataDir + "/Items.xml");
    }

    private static void serializeToJson(Object data, String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void serializeToXml(Object data, String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            XStream xstream = new XStream(new DomDriver());
            xstream.alias("data", List.class);
            xstream.toXML(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

