package com.qrfood.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private long id;
    private long restauranteId;          // novo: persisted as restaurante_id
    private int  mesaNumero;             // era tableId; mapeado do JSON mesaNumero
    private StatusPedido status = StatusPedido.EM_ESPERA;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime startedAt;
    private List<PedidoProduto> itens = new ArrayList<>();

    public long getId()                           { return id; }
    public void setId(long id)                    { this.id = id; }

    public long getRestauranteId()                { return restauranteId; }
    public void setRestauranteId(long rid)        { this.restauranteId = rid; }

    public int getMesaNumero()                    { return mesaNumero; }
    public void setMesaNumero(int mesaNumero)     { this.mesaNumero = mesaNumero; }

    public StatusPedido getStatus()               { return status; }
    public void setStatus(StatusPedido status)    { this.status = status; }

    public LocalDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(LocalDateTime t)     { this.createdAt = t; }

    public LocalDateTime getStartedAt()           { return startedAt; }
    public void setStartedAt(LocalDateTime t)     { this.startedAt = t; }

    public List<PedidoProduto> getItens()         { return itens; }
    public void setItens(List<PedidoProduto> l)   { this.itens = l; }
}