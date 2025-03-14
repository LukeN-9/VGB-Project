package com.vgb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Invoice {
    private UUID invoiceUuid;
    private LocalDate invoiceDate;
    private Company customer;
    private Person salesPerson;
    private List<Item> items;

    /** Constructor */
    public Invoice(String uuid, LocalDate date, Company customer, Person salesPerson, List<Item> items) {
        this.invoiceUuid = UUID.fromString(uuid);
        this.invoiceDate = date;
        this.customer = customer;
        this.salesPerson = salesPerson;
        this.items = (items != null) ? items : new ArrayList<>();
    }

    /** Getters */
    public UUID getInvoiceUuid() {
        return invoiceUuid;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public Company getCustomer() {
        return customer;
    }

    public Person getSalesPerson() {
        return salesPerson;
    }

    public List<Item> getItems() {
        return items;
    }

    /** Adds an item to the invoice */
    public void addItem(Item item) {
        if (item != null) {
            items.add(item);
        }
    }

    /** Calculates subtotal (before tax) */
    public double calculateSubtotal() {
        return roundToCent(items.stream().mapToDouble(Item::getCost).sum());
    }

    /** Calculates total tax */
    public double calculateTaxTotal() {
        return roundToCent(items.stream().mapToDouble(Item::getTax).sum());
    }

    /** Calculates grand total (subtotal + tax) */
    public double calculateGrandTotal() {
        return roundToCent(calculateSubtotal() + calculateTaxTotal());
    }

    /** Formats the invoice output */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Invoice#  %s\n", invoiceUuid));
        sb.append(String.format("Date      %s\n", invoiceDate));
        sb.append(customer.toString()).append("\n");
        sb.append(String.format("Sales Person: \n%s\n", salesPerson.toString())); 

        sb.append(String.format("Items (%d)\n", items.size()));
        sb.append("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-                          -=-=-=-=-=- -=-=-=-=-=-\n");

        double totalTax = 0;
        double totalAmount = 0;
        for (Item item : items) {
            totalTax += item.getTax();
            totalAmount += item.getCost();
            sb.append(item.toString()).append("\n");
        }

        sb.append("                                                    -=-=-=-=-=- -=-=-=-=-=-\n");
        sb.append(String.format("                         Subtotals $%10.2f $%10.2f\n", roundToCent(totalTax), roundToCent(totalAmount)));
        sb.append(String.format("                       Grand Total             $%10.2f\n", roundToCent(totalAmount + totalTax)));

        return sb.toString();
    }

    /** Helper method to round a value to the nearest cent */
    private double roundToCent(double price) {
        return Math.round(price * 100.0) / 100.0;
    }
}


