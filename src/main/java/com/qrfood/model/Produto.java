package com.qrfood.model;

import java.math.BigDecimal;

public class Produto {
    private long id;
    private long restauranteId;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String imagem;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(long restauranteId) { this.restauranteId = restauranteId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getImagem() { return imagem; }
    public void setImagem(String imagem) { this.imagem = imagem; }
}
