package com.qrfood.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilitário simples de conexão com o banco MySQL.
 * Centraliza URL, usuário e senha para evitar duplicação no código.
 */
public class ConexaoBD {

    private static final String URL =
        "jdbc:mysql://localhost:3306/qrfood?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";

    private static final String PASS = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            URL,
            USER,
            PASS
        );
    }
}