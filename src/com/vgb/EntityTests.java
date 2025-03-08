package com.vgb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import java.time.LocalDate;

public class EntityTests {

    public static final double TOLERANCE = 0.001;

    /**
     * Tests Equipment cost and tax calculations.
     */
    @Test
    public void testEquipment() {
        UUID uuid = UUID.randomUUID();
        Equipment equipment = new Equipment(uuid.toString(), "Excavator", "EX200", 95125.00);

        assertEquals(95125.00, equipment.getRetailPrice(), TOLERANCE);
        assertTrue(equipment.toString().contains("Excavator"));
    }

    /**
     * Tests Lease cost and tax calculations using startDate and endDate.
     */
    @Test
    public void testLease() {
        Equipment equipment = new Equipment(UUID.randomUUID().toString(), "Loader", "LD100", 80000.00);
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 1);

        double expectedLeaseCost = equipment.calculateLeaseCost(startDate, endDate);
        double expectedLeaseTax = equipment.calculateLeaseTax(startDate, endDate);
        
        assertEquals(58060.27, expectedLeaseCost, TOLERANCE);
        assertEquals(1500.00, expectedLeaseTax, TOLERANCE);
    }

    /**
     * Tests Rental cost and tax calculations.
     */
    @Test
    public void testRental() {
        Equipment equipment = new Equipment(UUID.randomUUID().toString(), "Crane", "CR300", 50000.00);
        double rentalHours = 25;

        double expectedRentalCost = equipment.calculateRentCost(rentalHours);
        double expectedRentalTax = equipment.calculateRentTax(expectedRentalCost);

        assertEquals(1250.0, expectedRentalCost, TOLERANCE);
        assertEquals(54.75, expectedRentalTax, TOLERANCE);
    }

    /**
     * Tests Material purchase and tax calculations including units.
     */
    @Test
    public void testMaterial() {
        Material material = new Material(UUID.randomUUID().toString(), "Steel", "Ton", 9.99);
        int quantity = 31;

        double expectedTotalCost = material.calculateMaterialCost(quantity);
        double expectedTax = material.calculateMaterialTax(quantity);

        assertEquals(309.69, expectedTotalCost, TOLERANCE);
        assertEquals(22.14, expectedTax, TOLERANCE);
    }

    /**
     * Tests Contract toString() output.
     */
    @Test
    public void testContractToString() {
        UUID contractUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();
        Contract contract = new Contract(contractUuid.toString(), "Foundation Pour", companyUuid.toString());

        String expectedString = "Contract: Foundation Pour (Company UUID: " + companyUuid + ")";
        assertEquals(expectedString, contract.toString());
        assertTrue(contract.toString().contains("Foundation Pour"));
        assertTrue(contract.toString().contains(companyUuid.toString()));
    }
}

