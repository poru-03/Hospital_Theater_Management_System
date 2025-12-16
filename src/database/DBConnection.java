package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // 1. Database Credentials
    private static final String URL = "jdbc:mysql://Localhost:3306/hospital_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // 2. The Connection Method
    public static Connection getConnection() {
        Connection con = null;
        try {
            // Load the MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the link
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database Connected Successfully!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        }
        return con;
    }
}
