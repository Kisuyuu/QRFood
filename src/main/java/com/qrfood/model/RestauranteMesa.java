package com.qrfood.model;

public class RestauranteMesa {
    private long id;
    private long restauranteId;
    private int numero;
    private String qrCode; // URL armazenada

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(long restauranteId) { this.restauranteId = restauranteId; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}
