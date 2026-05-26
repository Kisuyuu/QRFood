package com.qrfood.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private long id;
    private long tableId;
    private StatusPedido status = StatusPedido.EM_ESPERA;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime startedAt;
    private List<PedidoProduto> itens = new ArrayList<>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getTableId() { return tableId; }
    public void setTableId(long tableId) { this.tableId = tableId; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public List<PedidoProduto> getItens() { return itens; }
    public void setItens(List<PedidoProduto> itens) { this.itens = itens; }
}
