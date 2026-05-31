package com.qrfood.model;

public class Usuario {
    private long id;
    private String nome;
    private String email;
    private String senhaHash;
    private String salt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
}
