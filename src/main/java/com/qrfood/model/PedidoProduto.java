package com.qrfood.model;

public class PedidoProduto {
    private long   id;
    private long   pedidoId;
    private long   produtoId;
    private String nomeProduto; // preenchido via JOIN, não persistido em coluna própria
    private String observacao;
    private int    quantidade;

    public long getId()                        { return id; }
    public void setId(long id)                 { this.id = id; }

    public long getPedidoId()                  { return pedidoId; }
    public void setPedidoId(long pedidoId)     { this.pedidoId = pedidoId; }

    public long getProdutoId()                 { return produtoId; }
    public void setProdutoId(long produtoId)   { this.produtoId = produtoId; }

    public String getNomeProduto()             { return nomeProduto; }
    public void setNomeProduto(String nome)    { this.nomeProduto = nome; }

    public String getObservacao()              { return observacao; }
    public void setObservacao(String obs)      { this.observacao = obs; }

    public int getQuantidade()                 { return quantidade; }
    public void setQuantidade(int quantidade)  { this.quantidade = quantidade; }
}