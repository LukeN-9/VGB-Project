package com.vgb;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a collection of utility methods that define a general API for
 * interacting with the database supporting this application.
 * Each method in this class opens its own JDBC Connection and cleans up
 * resources automatically via try‐with‐resources. All operations are logged
 * to provide traceability and error diagnosis.
 */
public class InvoiceData {
    private static final Logger log = LogManager.getLogger(InvoiceData.class);

    /**
     * Removes all records from all tables in the database.
     * Disables foreign‐key checks, truncates each table in reverse‐dependency
     * order, then re‐enables checks and commits.
     */
    public static void clearDatabase() {
        String[] tables = {
            "InvoiceItems", "Invoice", "Item",
            "Company", "Email", "Address",
            "ZipCode", "State", "Person"
        };
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(false);
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            for (String tbl : tables) {
                // delete rather than drop so table definitions remain
                stmt.executeUpdate("DELETE FROM " + tbl);
            }
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            conn.commit();
            log.info("Cleared all data from database");
        } catch (SQLException e) {
            log.error("Error clearing database", e);
        }
    }

    /**
     * Method to add a person record to the database with the provided data.
     *
     * @param personUuid the UUID of the person
     * @param firstName  first name of the person
     * @param lastName   last name of the person
     * @param phone      phone number of the person
     */
    public static void addPerson(UUID personUuid, String firstName, String lastName, String phone) {
        String sql = "INSERT INTO Person(uuid, firstName, lastName, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, personUuid.toString());
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, phone);
            ps.executeUpdate();
            log.info("Added Person {}", personUuid);
        } catch (SQLException e) {
            log.error("Error adding Person {}", personUuid, e);
        }
    }

    /**
     * Adds an email record corresponding to the provided person UUID.
     *
     * @param personUuid the UUID of the person
     * @param email      the email address to add
     */
    public static void addEmail(UUID personUuid, String email) {
        String sql = "INSERT INTO Email(emailAddress, personUuid) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, personUuid.toString());
            ps.executeUpdate();
            log.info("Added Email {} for Person {}", email, personUuid);
        } catch (SQLException e) {
            log.error("Error adding Email {} for Person {}", email, personUuid, e);
        }
    }
    
    /**
     * Inserts a state code and name into the State table.
     * Skips if that stateCode already exists.
     */
    public static void addState(String stateCode, String stateName) {
        String sql = "INSERT INTO State(stateCode, name) VALUES(?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stateCode);
            ps.setString(2, stateName);
            ps.executeUpdate();
            log.info("Added State {} ({})", stateCode, stateName);
        } catch (SQLIntegrityConstraintViolationException dup) {
            // already exists—no action needed
            log.debug("State {} already exists, skipping", stateCode);
        } catch (SQLException e) {
            log.error("Error adding State {}", stateCode, e);
        }
    }


    /**
     * Inserts a zip code and city into the ZipCode table.
     * Skips if that zipCode already exists.
     */
    public static void addZipCode(String zipCode, String city) {
        String sql = "INSERT INTO ZipCode(zipCode, city) VALUES(?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, zipCode);
            ps.setString(2, city);
            ps.executeUpdate();
            log.info("Added ZipCode {} ({})", zipCode, city);
        } catch (SQLIntegrityConstraintViolationException dup) {
            // already exists—no action needed
            log.debug("ZipCode {} already exists, skipping", zipCode);
        } catch (SQLException e) {
            log.error("Error adding ZipCode {}", zipCode, e);
        }
    }


    /**
     * Adds a company record, ensuring its address can be inserted
     * by first seeding State and ZipCode, then inserting/reusing Address,
     * and finally inserting Company(uuid, contactUuid, name, addressId).
     */
    public static void addCompany(UUID companyUuid,
                                  UUID contactUuid,
                                  String name,
                                  String street,
                                  String city,
                                  String stateCode,
                                  String zipCode) {
        // 0) ensure referenced PKs exist
        addState(stateCode, stateCode);  // using code as placeholder name
        addZipCode(zipCode, city);

        int addressId = -1;

        try (Connection conn = DBConnection.getConnection()) {
            // 1) Try to find existing Address
            String lookupSql =
                "SELECT id FROM Address WHERE street=? AND city=? AND stateCode=? AND zipCode=?";
            try (PreparedStatement lookup = conn.prepareStatement(lookupSql)) {
                lookup.setString(1, street);
                lookup.setString(2, city);
                lookup.setString(3, stateCode);
                lookup.setString(4, zipCode);
                try (ResultSet rs = lookup.executeQuery()) {
                    if (rs.next()) {
                        addressId = rs.getInt("id");
                        log.info("Found existing Address id={} for Company {}", addressId, companyUuid);
                    }
                }
            }

            // 2) If not found, insert a new Address
            if (addressId < 0) {
                String insertAddrSql =
                    "INSERT INTO Address(street, city, stateCode, zipCode) VALUES(?, ?, ?, ?)";
                try (PreparedStatement insertAddr = conn.prepareStatement(insertAddrSql,
                         Statement.RETURN_GENERATED_KEYS)) {
                    insertAddr.setString(1, street);
                    insertAddr.setString(2, city);
                    insertAddr.setString(3, stateCode);
                    insertAddr.setString(4, zipCode);
                    insertAddr.executeUpdate();
                    try (ResultSet keys = insertAddr.getGeneratedKeys()) {
                        if (keys.next()) {
                            addressId = keys.getInt(1);
                            log.info("Inserted new Address id={} for Company {}", addressId, companyUuid);
                        }
                    }
                }
            }

            // 3) Sanity check
            if (addressId < 0) {
                log.error("Could not determine addressId for {} – aborting Company insert", companyUuid);
                return;
            }

            // 4) Insert Company row
            String insertCompSql =
                "INSERT INTO Company(uuid, contactUuid, name, addressId) VALUES(?, ?, ?, ?)";
            try (PreparedStatement insertComp = conn.prepareStatement(insertCompSql)) {
                insertComp.setString(1, companyUuid.toString());
                insertComp.setString(2, contactUuid.toString());
                insertComp.setString(3, name);
                insertComp.setInt(4, addressId);
                insertComp.executeUpdate();
                log.info("Added Company {} (name='{}') with addressId={}", companyUuid, name, addressId);
            } catch (SQLIntegrityConstraintViolationException pkEx) {
                log.error("Cannot add Company {}; UUID already exists", companyUuid, pkEx);
            }

        } catch (SQLException e) {
            log.error("Error in addCompany for {}", companyUuid, e);
        }
    }

    /**
     * Adds an equipment item record to the database.
     *
     * @param equipmentUuid UUID of the equipment
     * @param name          name/description of the equipment
     * @param modelNumber   model number
     * @param retailPrice   retail price
     */
    public static void addEquipment(UUID equipmentUuid, String name,
                                    String modelNumber, double retailPrice) {
        String sql = "INSERT INTO Item(uuid, type, name, field1, field2, field3) "
                   + "VALUES(?, 'E', ?, ?, ?, NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, equipmentUuid.toString());
            ps.setString(2, name);
            ps.setString(3, modelNumber);
            ps.setString(4, Double.toString(retailPrice));
            ps.executeUpdate();
            log.info("Added Equipment {} (name='{}')", equipmentUuid, name);

        } catch (SQLIntegrityConstraintViolationException pkEx) {
            // Primary key (uuid) violation
            log.error("Cannot add Equipment {}; UUID already exists", equipmentUuid, pkEx);
        } catch (SQLException e) {
            log.error("Error adding Equipment {}", equipmentUuid, e);
        }
    }


    /**
     * Adds a material item record to the database.
     *
     * @param materialUuid  UUID of the material
     * @param name          name/description
     * @param unit          unit of measure
     * @param pricePerUnit  cost per unit
     */
    public static void addMaterial(UUID materialUuid, String name,
                                   String unit, double pricePerUnit) {
        String sql = "INSERT INTO Item(uuid, type, name, field1, field2, field3) "
                   + "VALUES(?, 'M', ?, ?, ?, NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, materialUuid.toString());
            ps.setString(2, name);
            ps.setString(3, unit);
            ps.setString(4, Double.toString(pricePerUnit));
            ps.executeUpdate();
            log.info("Added Material {} (name='{}')", materialUuid, name);

        } catch (SQLIntegrityConstraintViolationException pkEx) {
            // Primary key (uuid) violation
            log.error("Cannot add Material {}; UUID already exists", materialUuid, pkEx);
        } catch (SQLException e) {
            log.error("Error adding Material {}", materialUuid, e);
        }
    }


    /**
     * Adds a contract item record to the database.
     *
     * @param contractUuid  UUID of the contract
     * @param name          description/name of the contract
     * @param servicerUuid  UUID of the servicing company/person
     */
    public static void addContract(UUID contractUuid, String name, UUID servicerUuid) {
        String sql = "INSERT INTO Item(uuid, type, name, field1, field2, field3) "
                   + "VALUES(?, 'C', ?, ?, NULL, NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, contractUuid.toString());
            ps.setString(2, name);
            ps.setString(3, servicerUuid.toString());
            ps.executeUpdate();
            log.info("Added Contract {} (name='{}')", contractUuid, name);

        } catch (SQLIntegrityConstraintViolationException pkEx) {
            // Primary key (uuid) violation
            log.error("Cannot add Contract {}; UUID already exists", contractUuid, pkEx);
        } catch (SQLException e) {
            log.error("Error adding Contract {}", contractUuid, e);
        }
    }


    /**
     * Adds an invoice record to the database.
     *
     * @param invoiceUuid      UUID of the invoice
     * @param customerUuid     UUID of the customer company
     * @param salesPersonUuid  UUID of the salesperson
     * @param date             invoice date
     */
    public static void addInvoice(UUID invoiceUuid, UUID customerUuid,
                                  UUID salesPersonUuid, LocalDate date) {
        String sql = "INSERT INTO Invoice(uuid, customerUuid, salesPersonUuid, date) "
                   + "VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoiceUuid.toString());
            ps.setString(2, customerUuid.toString());
            ps.setString(3, salesPersonUuid.toString());
            ps.setDate(4, Date.valueOf(date));
            ps.executeUpdate();
            log.info("Added Invoice {}", invoiceUuid);
        } catch (SQLException e) {
            log.error("Error adding Invoice {}", invoiceUuid, e);
        }
    }

    /**
     * Adds an equipment purchase linking an item to an invoice.
     *
     * @param invoiceUuid  UUID of the invoice
     * @param itemUuid     UUID of the equipment item
     */
    public static void addEquipmentPurchaseToInvoice(UUID invoiceUuid, UUID itemUuid) {
        String sql = "INSERT INTO InvoiceItems(invoiceUuid, itemUuid, field1, field2, field3) "
                   + "VALUES(?, ?, 'P', NULL, NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoiceUuid.toString());
            ps.setString(2, itemUuid.toString());
            ps.executeUpdate();
            log.info("Linked Equipment purchase {} to Invoice {}", itemUuid, invoiceUuid);
        } catch (SQLException e) {
            log.error("Error linking Equipment purchase {} to Invoice {}", itemUuid, invoiceUuid, e);
        }
    }

    /**
     * Adds an equipment lease linking an item to an invoice.
     *
     * @param invoiceUuid  UUID of the invoice
     * @param itemUuid     UUID of the equipment item
     * @param start        lease start date
     * @param end          lease end date
     */
    public static void addEquipmentLeaseToInvoice(UUID invoiceUuid, UUID itemUuid,
                                                  LocalDate start, LocalDate end) {
        String sql = "INSERT INTO InvoiceItems(invoiceUuid, itemUuid, field1, field2, field3) "
                   + "VALUES(?, ?, 'L', ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoiceUuid.toString());
            ps.setString(2, itemUuid.toString());
            ps.setString(3, start.toString());
            ps.setString(4, end.toString());
            ps.executeUpdate();
            log.info("Linked Equipment lease {} to Invoice {}", itemUuid, invoiceUuid);
        } catch (SQLException e) {
            log.error("Error linking Equipment lease {} to Invoice {}", itemUuid, invoiceUuid, e);
        }
    }

    /**
     * Adds an equipment rental linking an item to an invoice.
     *
     * @param invoiceUuid     UUID of the invoice
     * @param itemUuid        UUID of the equipment item
     * @param numberOfHours   rental duration in hours
     */
    public static void addEquipmentRentalToInvoice(UUID invoiceUuid, UUID itemUuid,
                                                   double numberOfHours) {
        String sql = "INSERT INTO InvoiceItems(invoiceUuid, itemUuid, field1, field2, field3) "
                   + "VALUES(?, ?, 'R', ?, NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoiceUuid.toString());
            ps.setString(2, itemUuid.toString());
            ps.setString(3, Double.toString(numberOfHours));
            ps.executeUpdate();
            log.info("Linked Equipment rental {} to Invoice {}", itemUuid, invoiceUuid);
        } catch (SQLException e) {
            log.error("Error linking Equipment rental {} to Invoice {}", itemUuid, invoiceUuid, e);
        }
    }

    /**
     * Adds a material purchase linking an item to an invoice.
     *
     * @param invoiceUuid    UUID of the invoice
     * @param itemUuid       UUID of the material item
     * @param numberOfUnits  quantity purchased
     */
    public static void addMaterialToInvoice(UUID invoiceUuid, UUID itemUuid, int numberOfUnits) {
        String sql = "INSERT INTO InvoiceItems(invoiceUuid, itemUuid, field1, field2, field3) "
                   + "VALUES(?, ?, ?, NULL, NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoiceUuid.toString());
            ps.setString(2, itemUuid.toString());
            ps.setString(3, Integer.toString(numberOfUnits));
            ps.executeUpdate();
            log.info("Linked Material {} to Invoice {}", itemUuid, invoiceUuid);
        } catch (SQLException e) {
            log.error("Error linking Material {} to Invoice {}", itemUuid, invoiceUuid, e);
        }
    }

    /**
     * Adds a contract charge linking an item to an invoice.
     *
     * @param invoiceUuid  UUID of the invoice
     * @param itemUuid     UUID of the contract item
     * @param amount       contract amount charged
     */
    public static void addContractToInvoice(UUID invoiceUuid, UUID itemUuid, double amount) {
        String sql = "INSERT INTO InvoiceItems(invoiceUuid, itemUuid, field1, field2, field3) "
                   + "VALUES(?, ?, ?, NULL, NULL)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoiceUuid.toString());
            ps.setString(2, itemUuid.toString());
            ps.setString(3, Double.toString(amount));
            ps.executeUpdate();
            log.info("Linked Contract {} to Invoice {}", itemUuid, invoiceUuid);
        } catch (SQLException e) {
            log.error("Error linking Contract {} to Invoice {}", itemUuid, invoiceUuid, e);
        }
    }
}
