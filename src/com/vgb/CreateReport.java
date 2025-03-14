package com.vgb;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

public class CreateReport {
    private List<Invoice> invoices;

    public CreateReport(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public void generateSummaryReport() {
        System.out.println("+----------------------------------------------------------------------------------------+");
        System.out.printf("| %-85s |\n", "Summary Report - By Total");
        System.out.println("+----------------------------------------------------------------------------------------+");
        System.out.printf("%-12s %-32s %-12s %-10s %-10s\n", "Invoice #", "Customer", "Num Items", "Tax", "Total");

        double totalTax = 0;
        double grandTotal = 0;
        int totalInvoices = invoices.size();

        invoices.sort(Comparator.comparingDouble(Invoice::calculateGrandTotal).reversed());

        for (Invoice invoice : invoices) {
            double tax = invoice.calculateTaxTotal();
            double total = invoice.calculateGrandTotal();
            int numItems = invoice.getItems().size();
            totalTax += tax;
            grandTotal += total;

            System.out.printf("%-12s %-32s %-12d $%10.2f $%10.2f\n",
                    invoice.getInvoiceUuid(),
                    invoice.getCustomer().getName(),
                    numItems,
                    tax,
                    total);
        }

        System.out.println("+----------------------------------------------------------------------------------------+");
        System.out.printf("%-45s %12d $%10.2f $%10.2f\n\n", "", totalInvoices, totalTax, grandTotal);
    }

    public void generateCustomerReport() {
        System.out.println("+----------------------------------------------------------------+");
        System.out.printf("| %-60s |\n", "Company Invoice Summary Report");
        System.out.println("+----------------------------------------------------------------+");
        System.out.printf("%-32s %-12s %-10s\n", "Company", "# Invoices", "Grand Total");

        Map<Company, List<Invoice>> customerInvoices = invoices.stream()
                .collect(Collectors.groupingBy(Invoice::getCustomer));

        double overallGrandTotal = 0;
        int totalInvoiceCount = 0;

        for (Map.Entry<Company, List<Invoice>> entry : customerInvoices.entrySet()) {
            Company customer = entry.getKey();
            List<Invoice> customerInvoiceList = entry.getValue();
            double grandTotal = customerInvoiceList.stream().mapToDouble(Invoice::calculateGrandTotal).sum();

            System.out.printf("%-32s %12d $%10.2f\n",
                    customer.getName(),
                    customerInvoiceList.size(),
                    grandTotal);

            overallGrandTotal += grandTotal;
            totalInvoiceCount += customerInvoiceList.size();
        }

        System.out.println("+----------------------------------------------------------------+");
        System.out.printf("%-32s %12d $%10.2f\n\n", "", totalInvoiceCount, overallGrandTotal);
    }

    public void generateDetailedInvoiceReport() {
        System.out.println("+----------------------------------------------------------------+");
        System.out.printf("| %-60s |\n", "Detailed Invoice Report");
        System.out.println("+----------------------------------------------------------------+\n");

        for (Invoice invoice : invoices) {
            System.out.println(invoice.toString());
        }
    }
}
