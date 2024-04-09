import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
public class WhatIsJdbc {
    public static void main(String[] args) {
        try {
            // Load the SQLite JDBC driver
            // Define the JDBC URL
            String url = "jdbc:sqlite:testDatabase.db";

            // Establish connection to the SQLite database
            try (Connection conn = DriverManager.getConnection(url)) {
                Statement stmt = conn.createStatement();
                System.out.println("Connection to SQLite has been established.");
                String q1 = "select * from Player";
                ResultSet rs = stmt.executeQuery(q1);
                while (rs.next()) {
                    System.out.println(rs.getInt("id") + ", " + rs.getString("name") + ", " + rs.getInt("money") + ", " + rs.getInt("current_game") + ", " + rs.getInt("curr_bet"));
                }
            }
        } catch (SQLException e) {
            throw new Error("Problem", e);
        }
    }
}