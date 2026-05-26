package com.qrfood.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/qrfood?useSSL=false&serverTimezone=UTC";

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("QRFOOD_DB_URL");
        String user = System.getenv("QRFOOD_DB_USER");
        String pass = System.getenv("QRFOOD_DB_PASS");

        if (url == null || url.isEmpty()) {
            url = DEFAULT_URL;
        }

        return DriverManager.getConnection(url, user, pass);
    }
}
