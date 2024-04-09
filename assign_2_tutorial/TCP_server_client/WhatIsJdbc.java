import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class WhatIsJdbc {
    public static void main(String[] args) {
        try {
            // Load the SQLite JDBC driver
            // Define the JDBC URL
            String url = "jdbc:sqlite:test.db";

            // Establish connection to the SQLite database
            try (Connection conn = DriverManager.getConnection(url)) {
                System.out.println("Connection to SQLite has been established.");
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
}