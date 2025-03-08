package com.vgb;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

public class Invoice {
    private UUID invoiceUuid;
    private LocalDate invoiceDate;
    private Company customer;
    private List<Item> items;

    public Invoice(String uuid, LocalDate date, Company customer, List<Item> items) {
        this.invoiceUuid = UUID.fromString(uuid);
        this.invoiceDate = date;
        this.customer = customer;
        this.items = items;
    }

    public UUID getInvoiceUuid() {
        return invoiceUuid;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public Company getCustomer() {
        return customer;
    }

    public List<Item> getItems() {
        return items;
    }

    // ======= Subtotal Overloaded Methods =======

    /** 
     * Base subtotal calculation for regular purchases 
     */
    public double calculateSubtotal() {
        return calculateSubtotal(null, null, 0, 0);
    }

    /** 
     * Overloaded subtotal for leased equipment 
     */
    public double calculateSubtotal(LocalDate startDate, LocalDate endDate) {
        return calculateSubtotal(startDate, endDate, 0, 0);
    }

    /** 
     * Overloaded subtotal for rented equipment 
     */
    public double calculateSubtotal(double rentalHours) {
        return calculateSubtotal(null, null, rentalHours, 0);
    }

    /** 
     * Overloaded subtotal for material with units 
     */
    public double calculateSubtotal(int materialUnits) {
        return calculateSubtotal(null, null, 0, materialUnits);
    }

    /** 
     * Fully parameterized subtotal calculation 
     */
    public double calculateSubtotal(LocalDate startDate, LocalDate endDate, double rentalHours, int materialUnits) {
        double subtotal = 0.0;
        for (Item item : items) {
            if (item instanceof Equipment) {
                Equipment equipment = (Equipment) item;
                if (startDate != null && endDate != null) {
                    subtotal += equipment.calculateLeaseCost(startDate, endDate);
                } else if (rentalHours > 0) {
                    subtotal += equipment.calculateRentCost(rentalHours);
                } else {
                    subtotal += equipment.getRetailPrice();
                }
            } else if (item instanceof Material) {
                subtotal += ((Material) item).calculateMaterialCost(materialUnits);
            } else if (item instanceof Contract) {
                subtotal += 10500; // Example contract price
            }
        }
        return roundToCent(subtotal);
    }

    // ======= Tax Overloaded Methods =======

    /** 
     * Base tax calculation 
     */
    public double calculateTaxTotal() {
        return calculateTaxTotal(null, null, 0, 0);
    }

    /** 
     * Overloaded tax for leased equipment 
     */
    public double calculateTaxTotal(LocalDate startDate, LocalDate endDate) {
        return calculateTaxTotal(startDate, endDate, 0, 0);
    }

    /** 
     * Overloaded tax for rented equipment 
     */
    public double calculateTaxTotal(double rentalHours) {
        return calculateTaxTotal(null, null, rentalHours, 0);
    }

    /** 
     * Overloaded tax for material with units 
     */
    public double calculateTaxTotal(int materialUnits) {
        return calculateTaxTotal(null, null, 0, materialUnits);
    }

    /** 
     * Fully parameterized tax calculation 
     */
    public double calculateTaxTotal(LocalDate startDate, LocalDate endDate, double rentalHours, int materialUnits) {
        double taxTotal = 0.0;
        for (Item item : items) {
            if (item instanceof Equipment) {
                Equipment equipment = (Equipment) item;
                if (startDate != null && endDate != null) {
                    taxTotal += equipment.calculateLeaseTax(startDate, endDate);
                } else if (rentalHours > 0) {
                    double rentCost = equipment.calculateRentCost(rentalHours);
                    taxTotal += equipment.calculateRentTax(rentCost);
                } else {
                    taxTotal += equipment.equipmentTax(equipment.getRetailPrice());
                }
            } else if (item instanceof Material) {
                taxTotal += ((Material) item).calculateMaterialTax(materialUnits);
            }
        }
        return roundToCent(taxTotal);
    }

    // ======= Grand Total Overloaded Methods =======

    /** 
     * Base grand total calculation 
     */
    public double calculateGrandTotal() {
        return calculateGrandTotal(null, null, 0, 0);
    }

    /** 
     * Overloaded grand total for leased equipment 
     */
    public double calculateGrandTotal(LocalDate startDate, LocalDate endDate) {
        return calculateGrandTotal(startDate, endDate, 0, 0);
    }

    /** 
     * Overloaded grand total for rented equipment 
     */
    public double calculateGrandTotal(double rentalHours) {
        return calculateGrandTotal(null, null, rentalHours, 0);
    }

    /** 
     * Overloaded grand total for material with units 
     */
    public double calculateGrandTotal(int materialUnits) {
        return calculateGrandTotal(null, null, 0, materialUnits);
    }

    /** 
     * Fully parameterized grand total calculation 
     */
    public double calculateGrandTotal(LocalDate startDate, LocalDate endDate, double rentalHours, int materialUnits) {
        return roundToCent(calculateSubtotal(startDate, endDate, rentalHours, materialUnits) +
                           calculateTaxTotal(startDate, endDate, rentalHours, materialUnits));
    }

    private double roundToCent(double price) {
        return Math.round(price * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return toString(null, null, 0, 0); // Default case (no special handling)
    }

    /**
     * Overloaded toString() to include calculations based on leasing, renting, and material units.
     */
    public String toString(LocalDate startDate, LocalDate endDate, double rentalHours, int materialUnits) {
        StringBuilder sb = new StringBuilder();
        sb.append("Invoice ID: ").append(invoiceUuid).append("\n");
        sb.append("Date: ").append(invoiceDate).append("\n");
        sb.append("Customer:\n").append(customer.toString()).append("\n");
        sb.append("Items:\n");

        for (Item item : items) {
            sb.append("- ").append(item.toString()).append("\n");
        }

        // Calculate totals with provided parameters
        double subtotal = calculateSubtotal(startDate, endDate, rentalHours, materialUnits);
        double taxTotal = calculateTaxTotal(startDate, endDate, rentalHours, materialUnits);
        double grandTotal = calculateGrandTotal(startDate, endDate, rentalHours, materialUnits);

        sb.append("Subtotal: $").append(String.format("%.2f", subtotal)).append("\n");
        sb.append("Tax Total: $").append(String.format("%.2f", taxTotal)).append("\n");
        sb.append("Grand Total: $").append(String.format("%.2f", grandTotal)).append("\n");

        return sb.toString();
    }

}

