package com.vgb;

import java.io.IOException;
import java.util.List;

public class DataConverter {
    private List<Person> persons;
    private List<Company> companies;
    private List<Item> items;
    private List<Invoice> invoices;

    /** Loads all data from CSV files */
    public void loadAllData(String dataDir) throws IOException {
        persons = ParseData.parsePersons(dataDir + "/Persons.csv");
        companies = ParseData.parseCompanies(dataDir + "/Companies.csv", persons);
        items = ParseData.parseItems(dataDir + "/Items.csv");
        invoices = ParseData.parseInvoices(dataDir + "/Invoices.csv", companies, persons);
        ParseData.parseInvoiceItems(dataDir + "/InvoiceItems.csv", invoices, items);
    }

    public List<Person> getPersons() {
        return persons;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }
}

