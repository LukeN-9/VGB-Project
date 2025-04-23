// InvoiceReport.java
package com.vgb;

import java.sql.SQLException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main driver: loads data and produces reports.
 */
public class InvoiceReport {
	private static final Logger log = LogManager.getLogger(InvoiceReport.class);


    public static void main(String[] args) {
        DataLoader loader = new DataLoader();
        try {
            loader.loadAllData();
            List<Invoice> invoices = loader.getInvoices();
            CreateReport report = new CreateReport(invoices);
            report.generateSummaryReport();
            report.generateCustomerReport();
            report.generateDetailedInvoiceReport();
        } catch (SQLException e) {
            log.error("Database error in main", e);
        }
    }
}


