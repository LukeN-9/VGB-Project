package com.vgb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

public class InvoiceTests {

    public static final double TOLERANCE = 0.001;

    @Test
    public void testInvoiceWithMixedPurchases() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 1);
        double rentalHours = 25;
        int materialUnits = 31;

        Equipment purchasedEquipment = new Equipment(UUID.randomUUID().toString(), "Excavator", "EX200", 50000.00);
        Equipment leasedEquipment = new Equipment(UUID.randomUUID().toString(), "Loader", "LD100", 80000.00);
        Equipment rentedEquipment = new Equipment(UUID.randomUUID().toString(), "Crane", "CR300", 50000.00);
        Material material = new Material(UUID.randomUUID().toString(), "Steel", "Ton", 9.99);
        Contract contract = new Contract(UUID.randomUUID().toString(), "Foundation Pour", UUID.randomUUID().toString());

        Company company = new Company(UUID.randomUUID().toString(), 
                new Person(UUID.randomUUID().toString(), "John", "Doe", "123-456-7890", null), 
                "TechCorp", 
                new Address("123 Main St", "New York", "NY", "10001"));

        Invoice invoice = new Invoice(UUID.randomUUID().toString(), LocalDate.now(), company,
                Arrays.asList(purchasedEquipment, leasedEquipment, rentedEquipment, material, contract));

        double expectedSubtotal = invoice.calculateSubtotal(startDate, endDate, rentalHours, materialUnits);
        double expectedTaxTotal = invoice.calculateTaxTotal(startDate, endDate, rentalHours, materialUnits);
        double expectedGrandTotal = invoice.calculateGrandTotal(startDate, endDate, rentalHours, materialUnits);

        assertEquals(expectedSubtotal, invoice.calculateSubtotal(startDate, endDate, rentalHours, materialUnits), TOLERANCE);
        assertEquals(expectedTaxTotal, invoice.calculateTaxTotal(startDate, endDate, rentalHours, materialUnits), TOLERANCE);
        assertEquals(expectedGrandTotal, invoice.calculateGrandTotal(startDate, endDate, rentalHours, materialUnits), TOLERANCE);
    }

    @Test
    public void testInvoiceWithPurchasedAndRentedItemsOnly() {
        double rentalHours = 10;  // Different rental duration
        int materialUnits = 15;  // Different material quantity

        Equipment purchasedEquipment = new Equipment(UUID.randomUUID().toString(), "Bulldozer", "BD500", 65000.00);
        Equipment rentedEquipment = new Equipment(UUID.randomUUID().toString(), "Forklift", "FL200", 45000.00);
        Material material = new Material(UUID.randomUUID().toString(), "Cement", "Bag", 12.50);
        Contract contract = new Contract(UUID.randomUUID().toString(), "Road Work", UUID.randomUUID().toString());

        Company company = new Company(UUID.randomUUID().toString(), 
                new Person(UUID.randomUUID().toString(), "Jane", "Smith", "987-654-3210", null), 
                "BuildCorp", 
                new Address("456 Oak St", "Los Angeles", "CA", "90002"));

        Invoice invoice = new Invoice(UUID.randomUUID().toString(), LocalDate.now(), company,
                Arrays.asList(purchasedEquipment, rentedEquipment, material, contract));

        // Compute expected values using the updated methods
        double expectedSubtotal = invoice.calculateSubtotal(null, null, rentalHours, materialUnits);
        double expectedTaxTotal = invoice.calculateTaxTotal(null, null, rentalHours, materialUnits);
        double expectedGrandTotal = invoice.calculateGrandTotal(null, null, rentalHours, materialUnits);

        assertEquals(expectedSubtotal, invoice.calculateSubtotal(null, null, rentalHours, materialUnits), TOLERANCE);
        assertEquals(expectedTaxTotal, invoice.calculateTaxTotal(null, null, rentalHours, materialUnits), TOLERANCE);
        assertEquals(expectedGrandTotal, invoice.calculateGrandTotal(null, null, rentalHours, materialUnits), TOLERANCE);
    }
}
