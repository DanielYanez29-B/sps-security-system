package com.sps.security.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = System.getenv().getOrDefault(
            "SPS_DB_URL", "jdbc:mysql://localhost:3306/sps_db");
    private static final String USER = System.getenv().getOrDefault(
            "SPS_DB_USER", "sps_db");
    private static final String PASSWORD = System.getenv().getOrDefault(
            "SPS_DB_PASSWORD", "root");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
