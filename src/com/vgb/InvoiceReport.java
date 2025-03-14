package com.vgb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class InvoiceReport {
    public static void main(String[] args) {
        String dataDir = "data";
        DataConverter dataConverter = new DataConverter();

        try {
            // Load data
            dataConverter.loadAllData(dataDir);
            List<Invoice> invoices = dataConverter.getInvoices();
            List<Item> items = dataConverter.getItems();
            List<Person> persons = dataConverter.getPersons();
            List<Company> companies = dataConverter.getCompanies();

            // Generate reports
            CreateReport invoiceReport = new CreateReport(invoices);
            invoiceReport.generateSummaryReport();
            invoiceReport.generateCustomerReport();
            invoiceReport.generateDetailedInvoiceReport();
            
            serializeToJson(items, "data/items.json");
            serializeToXml(items, "data/items.xml");

            serializeToJson(persons, "data/persons.json");
            serializeToXml(persons, "data/persons.xml");

            serializeToJson(companies, "data/companies.json");
            serializeToXml(companies, "data/companies.xml");

        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private static void serializeToJson(Object data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void serializeToXml(Object data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            XStream xstream = new XStream(new DomDriver());
            xstream.alias("data", List.class);
            xstream.toXML(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

