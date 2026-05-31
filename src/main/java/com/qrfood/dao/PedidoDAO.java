package com.qrfood.dao;

import com.google.gson.Gson;
import com.qrfood.model.Pedido;
import com.qrfood.model.PedidoProduto;
import com.qrfood.model.StatusPedido;
import com.qrfood.util.ConexaoBD;
import com.qrfood.util.GsonFactory;
import com.qrfood.websocket.PedidoWebSocket;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private static final Gson gson = GsonFactory.create();

    // ─── Salvar novo pedido ──────────────────────────────────────────────────

    public long salvarPedido(Pedido pedido) throws Exception {
        if (pedido.getCreatedAt() == null) {
            pedido.setCreatedAt(LocalDateTime.now());
        }
        if (pedido.getStatus() == StatusPedido.EM_ANDAMENTO && pedido.getStartedAt() == null) {
            pedido.setStartedAt(LocalDateTime.now());
        }

        String sqlPedido =
            "INSERT INTO Pedido (restaurante_id, mesa_numero, status, created_at, started_at) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection c = ConexaoBD.getConnection()) {

            long pedidoId;

            try (PreparedStatement ps = c.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, pedido.getRestauranteId());
                ps.setInt(2, pedido.getMesaNumero());
                ps.setString(3, pedido.getStatus().name());
                ps.setTimestamp(4, Timestamp.valueOf(pedido.getCreatedAt()));
                ps.setTimestamp(5, pedido.getStartedAt() != null
                        ? Timestamp.valueOf(pedido.getStartedAt()) : null);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedidoId = rs.getLong(1);
                        pedido.setId(pedidoId);
                    } else {
                        throw new RuntimeException("Erro ao obter id do pedido");
                    }
                }
            }

            // Inserir itens
            String sqlItem =
                "INSERT INTO PedidoProduto (order_id, produto_id, observacao, quantidade) " +
                "VALUES (?, ?, ?, ?)";
            try (PreparedStatement pis = c.prepareStatement(sqlItem)) {
                for (PedidoProduto pp : pedido.getItens()) {
                    pis.setLong(1, pedidoId);
                    pis.setLong(2, pp.getProdutoId());
                    pis.setString(3, pp.getObservacao());
                    pis.setInt(4, pp.getQuantidade());
                    pis.addBatch();
                }
                pis.executeBatch();
            }

            // Enriquecer com nomes dos produtos para o WebSocket
            String sqlEnrich =
                "SELECT pp.id, pp.produto_id, prod.nome AS produto_nome, pp.observacao, pp.quantidade " +
                "FROM PedidoProduto pp " +
                "JOIN Produto prod ON prod.id = pp.produto_id " +
                "WHERE pp.order_id = ?";
            try (PreparedStatement es = c.prepareStatement(sqlEnrich)) {
                es.setLong(1, pedidoId);
                try (ResultSet er = es.executeQuery()) {
                    pedido.getItens().clear();
                    while (er.next()) {
                        PedidoProduto pp = new PedidoProduto();
                        pp.setId(er.getLong("id"));
                        pp.setPedidoId(pedidoId);
                        pp.setProdutoId(er.getLong("produto_id"));
                        pp.setNomeProduto(er.getString("produto_nome"));
                        pp.setObservacao(er.getString("observacao"));
                        pp.setQuantidade(er.getInt("quantidade"));
                        pedido.getItens().add(pp);
                    }
                }
            }

            // Notificar painel via WebSocket
            PedidoWebSocket.notificarRestaurante(
                String.valueOf(pedido.getRestauranteId()), pedido);

            return pedidoId;
        }
    }

    // ─── Listar pedidos ativos de um restaurante ─────────────────────────────

    public List<Pedido> listarPorRestaurante(long restauranteId) throws Exception {
        List<Pedido> pedidos = new ArrayList<>();

        String sql = """
            SELECT p.id, p.restaurante_id, p.mesa_numero, p.status,
                   p.created_at, p.started_at,
                   pp.id AS item_id, pp.produto_id,
                   prod.nome AS produto_nome,
                   pp.observacao, pp.quantidade
            FROM Pedido p
            LEFT JOIN PedidoProduto pp   ON pp.order_id  = p.id
            LEFT JOIN Produto       prod ON prod.id       = pp.produto_id
            WHERE p.restaurante_id = ?
              AND p.status != 'CONCLUIDO'
            ORDER BY p.created_at DESC
            """;

        try (Connection c = ConexaoBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, restauranteId);
            try (ResultSet rs = ps.executeQuery()) {
                long currentId = -1;
                Pedido current = null;

                while (rs.next()) {
                    long id = rs.getLong("id");

                    if (id != currentId) {
                        current = new Pedido();
                        current.setId(id);
                        current.setRestauranteId(rs.getLong("restaurante_id"));
                        current.setMesaNumero(rs.getInt("mesa_numero"));
                        current.setStatus(StatusPedido.valueOf(rs.getString("status")));

                        Timestamp createdAt = rs.getTimestamp("created_at");
                        if (createdAt != null) current.setCreatedAt(createdAt.toLocalDateTime());

                        Timestamp startedAt = rs.getTimestamp("started_at");
                        if (startedAt != null) current.setStartedAt(startedAt.toLocalDateTime());

                        pedidos.add(current);
                        currentId = id;
                    }

                    long itemId = rs.getLong("item_id");
                    if (!rs.wasNull() && current != null) {
                        PedidoProduto pp = new PedidoProduto();
                        pp.setId(itemId);
                        pp.setPedidoId(id);
                        pp.setProdutoId(rs.getLong("produto_id"));
                        pp.setNomeProduto(rs.getString("produto_nome"));
                        pp.setObservacao(rs.getString("observacao"));
                        pp.setQuantidade(rs.getInt("quantidade"));
                        current.getItens().add(pp);
                    }
                }
            }
        }
        return pedidos;
    }

    // ─── Atualizar status ────────────────────────────────────────────────────

    public void atualizarStatus(long pedidoId, StatusPedido novoStatus) throws Exception {
        String sql = novoStatus == StatusPedido.EM_ANDAMENTO
            ? "UPDATE Pedido SET status = ?, started_at = NOW() WHERE id = ?"
            : "UPDATE Pedido SET status = ? WHERE id = ?";

        try (Connection c = ConexaoBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, novoStatus.name());
            ps.setLong(2, pedidoId);
            ps.executeUpdate();
        }
    }
}