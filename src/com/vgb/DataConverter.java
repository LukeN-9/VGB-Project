// DataConverter.java
package com.vgb;

import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Converts data loaded from flat files into JSON and XML.
 */
public class DataConverter {
	private static final Logger log = LogManager.getLogger(DataConverter.class);


    public static void main(String[] args) {
        String dataDir = "data";
        DataLoader data = new DataLoader();

        try {
            data.loadFromFiles(dataDir);
            List<Item> items     = data.getItems();
            List<Person> persons = data.getPersons();
            List<Company> companies = data.getCompanies();
            
            serializeToJson(items, "data/items.json");
            serializeToXml(items, "data/items.xml");

            serializeToJson(persons, "data/persons.json");
            serializeToXml(persons, "data/persons.xml");

            serializeToJson(companies, "data/companies.json");
            serializeToXml(companies, "data/companies.xml");

        } catch (IOException e) {
            log.error("Error loading data from flat files", e);
        }
    }

    private static void serializeToJson(Object data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
        } catch (IOException e) {
            log.error("JSON serialization failed", e);
        }
    }

    private static void serializeToXml(Object data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            XStream xstream = new XStream(new DomDriver());
            xstream.alias("data", List.class);
            xstream.toXML(data, writer);
        } catch (IOException e) {
            log.error("XML serialization failed", e);
        }
    }
}

