-- QRFood schema for MySQL (adapt types/indexes as needed)
-- Charset and engine
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS qrfood CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qrfood;

-- Usuario
CREATE TABLE IF NOT EXISTS Usuario (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  senha_hash VARCHAR(255) NOT NULL,
  salt VARCHAR(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Restaurante
CREATE TABLE IF NOT EXISTS Restaurante (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  nome VARCHAR(255) NOT NULL,
  descricao TEXT,
  FOREIGN KEY (user_id) REFERENCES Usuario(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- RestauranteMesa (mesa da restaurante)
CREATE TABLE IF NOT EXISTS RestauranteMesa (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  restaurante_id BIGINT NOT NULL,
  numero INT NOT NULL,
  qr_code TEXT,
  UNIQUE KEY ux_restaurante_numero (restaurante_id, numero),
  FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Produto
CREATE TABLE IF NOT EXISTS Produto (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  restaurante_id BIGINT NOT NULL,
  nome VARCHAR(255) NOT NULL,
  descricao TEXT,
  preco DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  imagem TEXT,
  FOREIGN KEY (restaurante_id) REFERENCES Restaurante(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS Pedido (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  table_id BIGINT NOT NULL,
  status ENUM('EM_ESPERA','EM_ANDAMENTO','CONCLUIDO') NOT NULL DEFAULT 'EM_ESPERA',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  started_at DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- foreign key to mesa
ALTER TABLE Pedido
  ADD CONSTRAINT fk_pedido_mesa FOREIGN KEY (table_id) REFERENCES RestauranteMesa(id) ON DELETE RESTRICT;

-- PedidoProduto
CREATE TABLE IF NOT EXISTS PedidoProduto (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  produto_id BIGINT NOT NULL,
  observacao TEXT,
  quantidade INT NOT NULL DEFAULT 1,
  FOREIGN KEY (order_id) REFERENCES Pedido(id) ON DELETE CASCADE,
  FOREIGN KEY (produto_id) REFERENCES Produto(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- Example inserts (optional)
-- INSERT INTO Usuario (nome, email, senha_hash, salt) VALUES ('Admin', 'admin@qrfood.local', 'hash_here', 'salt_here');
