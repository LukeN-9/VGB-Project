// DBConnection.java
package com.vgb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility for obtaining a JDBC Connection to MySQL.
 */
public class DBConnection {
	private static final Logger log = LogManager.getLogger(DBConnection.class);


    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            log.info("MySQL JDBC Driver registered");
        } catch (ClassNotFoundException e) {
            log.error("MySQL JDBC Driver not found on classpath", e);
            throw new RuntimeException(e);
        }
    }

    private static final String URL      = "jdbc:mysql://nuros.unl.edu:3306/lnash3?serverTimezone=UTC";
    private static final String USER     = "lnash3";
    private static final String PASSWORD = "LukeSQL1!";

    /**
     * Opens and returns a new Connection.
     *
     * @return a live JDBC Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        log.debug("Opening database connection to {}", URL);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


