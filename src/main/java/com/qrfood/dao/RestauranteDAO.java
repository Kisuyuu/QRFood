package com.qrfood.dao;

import com.qrfood.model.Restaurante;
import com.qrfood.util.ConexaoBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RestauranteDAO {

    public Restaurante buscarPorUsuario(long usuarioId) throws Exception {

        String sql = """
            SELECT *
            FROM Restaurante
            WHERE user_id = ?
        """;

        try (
            Connection conn = ConexaoBD.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setLong(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    Restaurante restaurante = new Restaurante();

                    restaurante.setId(rs.getLong("id"));
                    restaurante.setUserId(rs.getLong("user_id"));
                    restaurante.setNome(rs.getString("nome"));
                    restaurante.setDescricao(rs.getString("descricao"));

                    return restaurante;
                }

                return null;
            }
        }
    }
}