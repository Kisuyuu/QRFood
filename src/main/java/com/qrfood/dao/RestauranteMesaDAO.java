package com.qrfood.dao;

import com.qrfood.util.ConexaoBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RestauranteMesaDAO {

    public Long buscarRestauranteIdPorMesa(
            long mesaNumero
    ) throws Exception {

        String sql = """
            SELECT restaurante_id
            FROM RestauranteMesa
            WHERE numero = ?
        """;

        try (
                Connection conn =
                        ConexaoBD.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(sql)
        ) {

            stmt.setLong(
                    1,
                    mesaNumero
            );

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getLong(
                            "restaurante_id"
                    );
                }

                return null;
            }
        }
    }

    // New helper: buscar restaurante pelo id da mesa (table id)
    public Long buscarRestauranteIdPorMesaId(long mesaId) throws Exception {
        String sql = "SELECT restaurante_id FROM RestauranteMesa WHERE id = ?";

        try (
                Connection conn = ConexaoBD.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, mesaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("restaurante_id");
                }
                return null;
            }
        }
    }
}