package com.qrfood.model;

public class PedidoProduto {
    private long id;
    private long pedidoId;
    private long produtoId;
    private String observacao;
    private int quantidade;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getPedidoId() { return pedidoId; }
    public void setPedidoId(long pedidoId) { this.pedidoId = pedidoId; }
    public long getProdutoId() { return produtoId; }
    public void setProdutoId(long produtoId) { this.produtoId = produtoId; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
