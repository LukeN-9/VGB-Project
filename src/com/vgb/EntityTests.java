package com.vgb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import java.util.UUID;

public class EntityTests {

    public static final double TOLERANCE = 0.001;

    /**
     * Tests Equipment cost and tax calculations.
     */
    @Test
    public void testEquipment() {
        UUID uuid = UUID.randomUUID();
        Equipment equipment = new Equipment(uuid.toString(), "Excavator", "EX200", 95125.00);

        assertEquals(95125.00, equipment.getCost(), TOLERANCE);
        assertEquals(4994.06, equipment.getTax(), TOLERANCE);
        assertTrue(equipment.toString().contains("Excavator"));
    }

    /**
     * Tests Lease cost and tax calculations using `Lease` subclass.
     */
    @Test
    public void testLease() {
        Equipment equipment = new Equipment(UUID.randomUUID().toString(), "Loader", "LD100", 80000.00);

        Lease lease = new Lease(UUID.randomUUID().toString(), equipment, "2024-01-01", "2026-06-01");

        assertEquals(58060.27, lease.getCost(), TOLERANCE);
        assertEquals(1500.00, lease.getTax(), TOLERANCE);
    }

    /**
     * Tests Rental cost and tax calculations using `Rental` subclass.
     */
    @Test
    public void testRental() {
        Equipment equipment = new Equipment(UUID.randomUUID().toString(), "Crane", "CR300", 50000.00);
        double rentalHours = 25;

        Rental rental = new Rental(UUID.randomUUID().toString(), equipment, rentalHours);

        assertEquals(1250.0, rental.getCost(), TOLERANCE);
        assertEquals(54.75, rental.getTax(), TOLERANCE);
    }

    /**
     * Tests Material purchase and tax calculations including units.
     */
    @Test
    public void testMaterial() {
        Material material = new Material(UUID.randomUUID().toString(), "Steel", "Ton", 9.99, 31);

        assertEquals(309.69, material.getCost(), TOLERANCE);
        assertEquals(22.14, material.getTax(), TOLERANCE);
    }

    /**
     * Tests Contract toString() output.
     */
    @Test
    public void testContractToString() {
        UUID contractUuid = UUID.randomUUID();
        UUID companyUuid = UUID.randomUUID();
        Contract contract = new Contract(contractUuid.toString(), "Foundation Pour", companyUuid.toString(), 10500);

        String expectedString = "Contract: Foundation Pour (Company UUID: " + companyUuid + ")";
        assertEquals(expectedString, contract.toString());
        assertTrue(contract.toString().contains("Foundation Pour"));
        assertTrue(contract.toString().contains(companyUuid.toString()));
    }
}
