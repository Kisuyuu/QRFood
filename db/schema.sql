DROP DATABASE IF EXISTS qrfood;

CREATE DATABASE qrfood
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE qrfood;

CREATE TABLE Usuario (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    salt       VARCHAR(255) NOT NULL
);

CREATE TABLE Restaurante (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id   BIGINT NOT NULL,
    nome      VARCHAR(255) NOT NULL,
    descricao TEXT,
    CONSTRAINT fk_restaurante_usuario
        FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
);

CREATE TABLE RestauranteMesa (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurante_id BIGINT NOT NULL,
    numero         INT NOT NULL,
    qr_code        TEXT,
    CONSTRAINT fk_mesa_restaurante
        FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id) ON DELETE CASCADE
);

CREATE TABLE Produto (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurante_id BIGINT NOT NULL,
    nome           VARCHAR(255) NOT NULL,
    descricao      TEXT,
    preco          DECIMAL(10,2) NOT NULL,
    imagem         TEXT,
    CONSTRAINT fk_produto_restaurante
        FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id) ON DELETE CASCADE
);

-- CHANGED: added restaurante_id FK; renamed column to mesa_numero (was ambiguous table_id)
CREATE TABLE Pedido (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurante_id BIGINT NOT NULL,
    mesa_numero    INT NOT NULL,
    status         ENUM('EM_ESPERA','EM_ANDAMENTO','CONCLUIDO') NOT NULL DEFAULT 'EM_ESPERA',
    created_at     DATETIME NOT NULL,
    started_at     DATETIME NULL,
    CONSTRAINT fk_pedido_restaurante
        FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id) ON DELETE CASCADE
);

CREATE TABLE PedidoProduto (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    observacao TEXT,
    quantidade INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_pedidoproduto_pedido
        FOREIGN KEY (order_id)   REFERENCES Pedido(id)  ON DELETE CASCADE,
    CONSTRAINT fk_pedidoproduto_produto
        FOREIGN KEY (produto_id) REFERENCES Produto(id) ON DELETE RESTRICT
);