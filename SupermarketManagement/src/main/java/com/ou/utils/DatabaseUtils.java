package com.ou.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {
    private final static String DRIVER = "com.mysql.cj.jdbc.Driver";
    private final static String HOST = "localhost";
    private final static String DATABASE = "SupermarketManagement";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "thanhnam";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", HOST, DATABASE), USERNAME, PASSWORD);
    }

}
