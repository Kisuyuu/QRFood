package com.qrfood.dao;

import com.qrfood.model.Produto;
import com.qrfood.util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    // ─── Listagem ────────────────────────────────────────────────────────────

    public List<Produto> listarPorRestaurante(long restauranteId) throws Exception {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM Produto WHERE restaurante_id = ? ORDER BY nome";

        try (Connection c = ConexaoBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, restauranteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) produtos.add(mapRow(rs));
            }
        }
        return produtos;
    }

    // ─── Inserção ────────────────────────────────────────────────────────────

    public long inserir(Produto produto) throws Exception {
        String sql =
            "INSERT INTO Produto (restaurante_id, nome, descricao, preco, imagem) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection c = ConexaoBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, produto.getRestauranteId());
            ps.setString(2, produto.getNome());
            ps.setString(3, produto.getDescricao());
            ps.setBigDecimal(4, produto.getPreco());
            ps.setString(5, produto.getImagem());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                throw new RuntimeException("Erro ao obter id do produto");
            }
        }
    }

    // ─── Atualização ─────────────────────────────────────────────────────────

    public void atualizar(Produto produto) throws Exception {
        String sql =
            "UPDATE Produto SET nome = ?, descricao = ?, preco = ?, imagem = ? " +
            "WHERE id = ? AND restaurante_id = ?";

        try (Connection c = ConexaoBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getDescricao());
            ps.setBigDecimal(3, produto.getPreco());
            ps.setString(4, produto.getImagem());
            ps.setLong(5, produto.getId());
            ps.setLong(6, produto.getRestauranteId());
            ps.executeUpdate();
        }
    }

    // ─── Exclusão ────────────────────────────────────────────────────────────

    public void excluir(long produtoId, long restauranteId) throws Exception {
        // restaurante_id garante que ninguém delete produto de outro restaurante
        String sql = "DELETE FROM Produto WHERE id = ? AND restaurante_id = ?";

        try (Connection c = ConexaoBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, produtoId);
            ps.setLong(2, restauranteId);
            ps.executeUpdate();
        }
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private Produto mapRow(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getLong("id"));
        p.setRestauranteId(rs.getLong("restaurante_id"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setPreco(rs.getBigDecimal("preco"));
        p.setImagem(rs.getString("imagem"));
        return p;
    }
}