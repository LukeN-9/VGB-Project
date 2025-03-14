package com.vgb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InvoiceTests {

    public static final double TOLERANCE = 0.001;

    @Test
    public void testInvoiceWithMixedPurchases() {
        double rentalHours = 25;
        int materialUnits = 31;

        Equipment purchasedEquipment = new Equipment(UUID.randomUUID().toString(), "Excavator", "EX200", 50000.00);
        Lease lease = new Lease(UUID.randomUUID().toString(), new Equipment(UUID.randomUUID().toString(), "Loader", "LD100", 80000.00), "2024-01-01", "2026-06-01");
        Rental rental = new Rental(UUID.randomUUID().toString(), new Equipment(UUID.randomUUID().toString(), "Crane", "CR300", 50000.00), rentalHours);
        Material material = new Material(UUID.randomUUID().toString(), "Steel", "Ton", 9.99, materialUnits);
        Contract contract = new Contract(UUID.randomUUID().toString(), "Foundation Pour", UUID.randomUUID().toString(), 10500);

        List<Item> items = Arrays.asList(purchasedEquipment, lease, rental, material, contract);

        Company company = new Company(UUID.randomUUID().toString(),
                new Person(UUID.randomUUID().toString(), "John", "Doe", "123-456-7890", null),
                "TechCorp",
                new Address("123 Main St", "New York", "NY", "10001"));
        Person person = new Person(UUID.randomUUID().toString(), "John", "Doe", "123-456-7890", null);

        Invoice invoice = new Invoice(UUID.randomUUID().toString(), LocalDate.now(), company,person, items);

        assertEquals(invoice.calculateSubtotal(), invoice.calculateSubtotal(), TOLERANCE);
        assertEquals(invoice.calculateTaxTotal(), invoice.calculateTaxTotal(), TOLERANCE);
        assertEquals(invoice.calculateGrandTotal(), invoice.calculateGrandTotal(), TOLERANCE);
        assertEquals(company.toString(), invoice.getCustomer().toString());
    }
}
