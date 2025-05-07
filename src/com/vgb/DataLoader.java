// DataLoader.java
package com.vgb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * Loads all domain objects (Person, Company, Item, Invoice, InvoiceItems)
 * from the MySQL database via JDBC.
 */
public class DataLoader {

	private static final Logger log = LogManager.getLogger(DataLoader.class);


    private List<Person> persons;
    private List<Company> companies;
    private List<Item> items;
    private List<Invoice> invoices;

    public void loadAllData() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            log.info("Loading persons");
            persons = loadPersons(conn);
            log.info("Loading companies");
            companies = loadCompanies(conn, persons);
            log.info("Loading items");
            items = loadItems(conn);
            log.info("Loading invoices");
            invoices = loadInvoices(conn, companies, persons);
            log.info("Loading invoice items");
            loadInvoiceItems(conn, invoices, items);
        } catch (SQLException e) {
            log.error("SQL error loading data", e);
            throw e;
        }
    }

    /** @return all loaded Person objects */
    public List<Person> getPersons() {
        return persons;
    }

    /** @return all loaded Company objects */
    public List<Company> getCompanies() {
        return companies;
    }

    /** @return all loaded Item objects */
    public List<Item> getItems() {
        return items;
    }

    /** @return all loaded Invoice objects */
    public List<Invoice> getInvoices() {
        return invoices;
    }

    /**
     * Executes a SELECT on the Person table and constructs Person instances.
     */
    private List<Person> loadPersons(Connection conn) throws SQLException {
        List<Person> list = new ArrayList<>();
        String sql = "SELECT uuid, firstName, lastName, phone, emails FROM Person";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String uuid  = rs.getString("uuid");
                String fn    = rs.getString("firstName");
                String ln    = rs.getString("lastName");
                String phone = rs.getString("phone");
                String ems   = rs.getString("emails");
                List<String> emails = (ems == null || ems.isEmpty())
                        ? Collections.emptyList()
                        : Arrays.asList(ems.split(";"));
                list.add(new Person(uuid, fn, ln, phone, emails));
            }
        }
        return list;
    }

    /**
     * Executes a SELECT on the Company table, finds each contact via persons list,
     * and constructs Company instances.
     */
    private List<Company> loadCompanies(Connection conn, List<Person> persons) throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql =
            "SELECT c.uuid, c.contactUuid, c.name, " +
            "       a.street, a.city, a.stateCode AS state, a.zipCode AS zip " +
            "  FROM Company c " +
            "  LEFT JOIN Address a ON c.addressId = a.id";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String uuid        = rs.getString("uuid");
                String contactUuid = rs.getString("contactUuid");
                String name        = rs.getString("name");
                String street      = rs.getString("street");
                String city        = rs.getString("city");
                String state       = rs.getString("state");
                String zip         = rs.getString("zip");

                // find the contact Person by UUID
                Person contact = persons.stream()
                        .filter(p -> p.getUuid().toString().equals(contactUuid))
                        .findFirst()
                        .orElse(null);

                Address address = new Address(street, city, state, zip);
                companies.add(new Company(uuid, contact, name, address));
            }
        }
        return companies;
    }

    /**
     * Reads all rows from Item and maps them to Java objects.
     * Uses the provided Connection rather than opening its own.
     */
    private List<Item> loadItems(Connection conn) throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT uuid, type, name, field1, field2, field3 FROM Item";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String uuid    = rs.getString("uuid");
                String type    = rs.getString("type");
                String name    = rs.getString("name");
                String f1raw   = rs.getString("field1");
                String f2raw   = rs.getString("field2");
                String f3raw   = rs.getString("field3");

                String f1 = (f1raw != null ? f1raw.trim() : "");
                String f2 = (f2raw != null ? f2raw.trim() : "");
                String f3 = (f3raw != null ? f3raw.trim() : "");

                switch (type) {
                    case "E":
                        double retailPrice = f2.isEmpty() ? 0.0 : Double.parseDouble(f2);
                        items.add(new Equipment(uuid, name, f1, retailPrice));
                        break;
                    case "M":
                        double unitPrice = f2.isEmpty() ? 0.0 : Double.parseDouble(f2);
                        int quantity     = f3.isEmpty() ? 0     : Integer.parseInt(f3);
                        items.add(new Material(uuid, name, f1, unitPrice, quantity));
                        break;
                    case "C":
                        double contractCost = f2.isEmpty() ? 0.0 : Double.parseDouble(f2);
                        items.add(new Contract(uuid, name, f1, contractCost));
                        break;
                    case "L":
                        if (!f1.isEmpty() && !f2.isEmpty()) {
                            items.add(new Lease(uuid, new Equipment(uuid, name, "", 0.0), f1, f2));
                        }
                        break;
                    case "R":
                        double hours = f2.isEmpty() ? 0.0 : Double.parseDouble(f2);
                        items.add(new Rental(uuid, new Equipment(uuid, name, "", 0.0), hours));
                        break;
                    default:
                        // unknown type, skip
                        break;
                }
            }
        }
        return items;
    }


    /**
     * Executes a SELECT on the Invoice table and constructs Invoice instances,
     * linking each to its Company and Person from the preloaded lists.
     */
    private List<Invoice> loadInvoices(Connection conn,
                                       List<Company> companies,
                                       List<Person> persons) throws SQLException {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT uuid, customerUuid, salesPersonUuid, date FROM Invoice";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String invUuid = rs.getString("uuid");
                String custUuid = rs.getString("customerUuid");
                String spUuid   = rs.getString("salesPersonUuid");
                LocalDate date  = LocalDate.parse(rs.getString("date"));
                Company cust = companies.stream()
                        .filter(c -> c.getUuid().toString().equals(custUuid))
                        .findFirst().orElse(null);
                Person sp = persons.stream()
                        .filter(p -> p.getUuid().toString().equals(spUuid))
                        .findFirst().orElse(null);
                list.add(new Invoice(invUuid, date, cust, sp, new ArrayList<>()));
            }
        }
        return list;
    }

    /**
     * Executes a SELECT on InvoiceItems and adds each item to its Invoice,
     * creating Rental or Lease objects when flagged, otherwise adding base Item.
     */
    private void loadInvoiceItems(Connection conn,
                                  List<Invoice> invoices,
                                  List<Item> items) throws SQLException {
        String sql = "SELECT invoiceUuid, itemUuid, field1, field2, field3 FROM InvoiceItems";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String invUuid = rs.getString("invoiceUuid");
                String itmUuid = rs.getString("itemUuid");
                String f1      = rs.getString("field1");
                String f2      = rs.getString("field2");
                String f3      = rs.getString("field3");
                Invoice inv = invoices.stream()
                        .filter(i -> i.getInvoiceUuid().toString().equals(invUuid))
                        .findFirst().orElse(null);
                Item base = items.stream()
                        .filter(i -> i.getUuid().equals(itmUuid))
                        .findFirst().orElse(null);
                if (inv != null && base != null) {
                    if (base instanceof Contract) {
                        ((Contract) base).setCost(Double.parseDouble(f1));
                        inv.addItem(base);
                    } else if (base instanceof Material) {
                        ((Material) base).setQuantity(Integer.parseInt(f1));
                        inv.addItem(base);
                    } else if (base instanceof Equipment) {
                        if ("R".equals(f1)) {
                            inv.addItem(new Rental(base.getUuid(), (Equipment) base, Double.parseDouble(f2)));
                        } else if ("L".equals(f1)) {
                            inv.addItem(new Lease(base.getUuid(), (Equipment) base, f2, f3));
                        } else {
                            inv.addItem(base);
                        }
                    } else {
                        inv.addItem(base);
                    }
                }
            }
        }
    }
    
    /**
     * Flat‐file loader (legacy Phase I–IV).
     *
     * @param dataDir path to directory containing Persons.csv, Companies.csv, etc.
     * @throws IOException if any file read fails
     */
    public void loadFromFiles(String dataDir) throws IOException {
        persons   = parsePersons(dataDir + "/Persons.csv");
        companies = parseCompanies(dataDir + "/Companies.csv", persons);
        items     = parseItems(dataDir + "/Items.csv");
        invoices  = parseInvoices(dataDir + "/Invoices.csv", companies, persons);
        parseInvoiceItems(dataDir + "/InvoiceItems.csv", invoices, items);
    }
    
    public static List<Person> parsePersons(String filePath) throws IOException {
        List<Person> persons = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        if (!lines.isEmpty()) lines.remove(0);
        for (String line : lines) {
            String[] parts = line.split(",");
            String uuid = parts[0];
            String firstName = parts[1];
            String lastName = parts[2];
            String phone = parts[3];
            List<String> emails = (parts.length > 4 && !parts[4].isEmpty())
                    ? Arrays.asList(parts[4].split(";"))
                    : new ArrayList<>();
            persons.add(new Person(uuid, firstName, lastName, phone, emails));
        }
        return persons;
    }

    public static List<Company> parseCompanies(String filePath, List<Person> persons) throws IOException {
        List<Company> companies = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        if (!lines.isEmpty()) lines.remove(0);
        for (String line : lines) {
            String[] parts = line.split(",");
            String uuid = parts[0];
            String contactUuid = parts[1];
            String name = parts[2];
            Address address = new Address(parts[3], parts[4], parts[5], parts[6]);
            Person contact = persons.stream()
                    .filter(p -> p.getUuid().toString().equals(contactUuid))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Person not found for contactUuid: " + contactUuid));
            companies.add(new Company(uuid, contact, name, address));
        }
        return companies;
    }

    public static List<Item> parseItems(String filePath) throws IOException {
        List<Item> items = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        if (!lines.isEmpty()) lines.remove(0);
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
                default:
                    // Leases and Rentals handled in parseInvoiceItems()
                    break;
            }
        }
        return items;
    }

    public static List<Invoice> parseInvoices(String filePath, List<Company> companies, List<Person> persons) throws IOException {
        List<Invoice> invoices = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        if (!lines.isEmpty()) lines.remove(0);
        for (String line : lines) {
            String[] parts = line.split(",");
            String invoiceUuid = parts[0];
            String customerUuid = parts[1];
            String salesPersonUuid = parts[2];
            LocalDate date = LocalDate.parse(parts[3]);

            Company customer = companies.stream()
                    .filter(c -> c.getUuid().toString().equals(customerUuid))
                    .findFirst()
                    .orElse(null);

            Person salesPerson = persons.stream()
                    .filter(p -> p.getUuid().toString().equals(salesPersonUuid))
                    .findFirst()
                    .orElse(null);

            invoices.add(new Invoice(invoiceUuid, date, customer, salesPerson, new ArrayList<>()));
        }
        return invoices;
    }

    public static void parseInvoiceItems(String filePath, List<Invoice> invoices, List<Item> items) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        if (!lines.isEmpty()) lines.remove(0);
        for (String line : lines) {
            String[] parts = line.split(",");
            String invoiceUuid = parts[0];
            String itemUuid = parts[1];

            Invoice invoice = invoices.stream()
                    .filter(inv -> inv.getInvoiceUuid().toString().equals(invoiceUuid))
                    .findFirst()
                    .orElse(null);

            Item baseItem = items.stream()
                    .filter(i -> i.getUuid().equals(itemUuid))
                    .findFirst()
                    .orElse(null);

            if (invoice != null && baseItem != null) {
                if (baseItem instanceof Contract) {
                    double cost = Double.parseDouble(parts[2]);
                    ((Contract) baseItem).setCost(cost);
                    invoice.addItem(baseItem);

                } else if (baseItem instanceof Material) {
                    int quantity = Integer.parseInt(parts[2]);
                    ((Material) baseItem).setQuantity(quantity);
                    invoice.addItem(baseItem);

                } else if (baseItem instanceof Equipment) {
                    // Rental: flag "R", hours in parts[3]
                    if ("R".equals(parts[2])) {
                        double hours = Double.parseDouble(parts[3]);
                        invoice.addItem(new Rental(itemUuid, (Equipment) baseItem, hours));
                    }
                    // Lease: flag "L", start=parts[3], end=parts[4]
                    else if ("L".equals(parts[2])) {
                        invoice.addItem(new Lease(itemUuid, (Equipment) baseItem, parts[3], parts[4]));
                    } else {
                        invoice.addItem(baseItem);
                    }

                } else {
                    invoice.addItem(baseItem);
                }
            }
        }
    }
}
