package com.qrfood.dao;

import com.google.gson.Gson;
import com.qrfood.model.Pedido;
import com.qrfood.model.PedidoProduto;
import com.qrfood.websocket.PedidoWebSocket;
import com.qrfood.util.ConexaoBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import com.qrfood.model.StatusPedido;
import java.sql.Timestamp;

public class PedidoDAO {
    private static final Gson gson = new Gson();

    public long salvarPedido(Pedido pedido, String restauranteId) throws Exception {
        // ensure timestamps are set: createdAt defaults to now if missing;
        // if status indicates work started, set startedAt when missing
        if (pedido.getCreatedAt() == null) {
            pedido.setCreatedAt(LocalDateTime.now());
        }
        if (pedido.getStatus() == StatusPedido.EM_ANDAMENTO && pedido.getStartedAt() == null) {
            pedido.setStartedAt(LocalDateTime.now());
        }
        String insertPedido = "INSERT INTO Pedido (table_id, status, created_at, started_at) VALUES (?, ?, ?, ?)";
        try (Connection c = ConexaoBD.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(insertPedido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, pedido.getTableId());
                ps.setString(2, pedido.getStatus().name());
                ps.setTimestamp(3, Timestamp.valueOf(pedido.getCreatedAt()));
                ps.setTimestamp(4, pedido.getStartedAt() != null ? Timestamp.valueOf(pedido.getStartedAt()) : null);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        long pedidoId = rs.getLong(1);
                        // inserir itens
                        String insertItem = "INSERT INTO PedidoProduto (order_id, produto_id, observacao, quantidade) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement pis = c.prepareStatement(insertItem)) {
                            for (PedidoProduto pp : pedido.getItens()) {
                                pis.setLong(1, pedidoId);
                                pis.setLong(2, pp.getProdutoId());
                                pis.setString(3, pp.getObservacao());
                                pis.setInt(4, pp.getQuantidade());
                                pis.addBatch();
                            }
                            pis.executeBatch();
                        }

                        // notificar via WebSocket (passar objeto serializável)
                        String pedidoJson = gson.toJson(pedido);
                        System.out.println("[PedidoDAO] notificarRestaurante payload: " + pedidoJson);
                        PedidoWebSocket.notificarRestaurante(restauranteId, pedido);

                        return pedidoId;
                    } else {
                        throw new RuntimeException("Erro ao obter id do pedido");
                    }
                }
            }
        }
    }
}
