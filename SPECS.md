# SPECS.md

## Tecnologias

### Backend
- Java 21
- Servlet (Jakarta EE 6.0)
- JDBC (MySQL Connector 8.0.33)
- Tomcat 10
- Maven (WAR)

### Frontend
- JSP (Jakarta Server Pages 3.1)
- JavaScript (Vanilla)
- CSS3 (variáveis, grid, flexbox, animações)

### Banco de Dados
- MySQL 8
- Schema normalizado (6 tabelas)

### Bibliotecas
- Gson 2.10.1 (serialização JSON)
- ZXing 3.5.1 (geração de QR Code)
- MySQL Connector J 8.0.33

---

## Arquitetura

CLIENTE (Navegador)
↓
JSP + JavaScript
↓
Servlet (Controller)
↓
DAO (Data Access Object)
↓
JDBC (Conexão)
↓
MySQL (Banco de Dados)
↓
WebSocket (Tempo Real)
↓
Painel Admin (Atualização)

text

### Camadas

| Camada | Responsabilidade | Localização |
|--------|------------------|-------------|
| **Servlet** | Receber requisições HTTP, validar dados, retornar respostas | `com.qrfood.servlet` |
| **DAO** | Acesso ao banco de dados, queries SQL, CRUD | `com.qrfood.dao` |
| **Model** | Entidades do sistema (POJOs) | `com.qrfood.model` |
| **WebSocket** | Comunicação em tempo real | `com.qrfood.websocket` |
| **Filter** | Segurança e controle de cache | `com.qrfood.filter` |
| **Util** | Utilitários (hash, QR Code, JSON) | `com.qrfood.util` |

---

## Entidades

### Usuario
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Chave primária |
| nome | VARCHAR(255) | Nome do usuário |
| email | VARCHAR(255) | Email único |
| senha_hash | VARCHAR(255) | Hash SHA-256 da senha |
| salt | VARCHAR(255) | Salt aleatório |

### Restaurante
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Chave primária |
| user_id | BIGINT | FK para Usuario |
| nome | VARCHAR(255) | Nome do restaurante |
| descricao | TEXT | Descrição |

### RestauranteMesa
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Chave primária |
| restaurante_id | BIGINT | FK para Restaurante |
| numero | INT | Número da mesa |
| qr_code | TEXT | URL do QR Code |

### Produto
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Chave primária |
| restaurante_id | BIGINT | FK para Restaurante |
| nome | VARCHAR(255) | Nome do produto |
| descricao | TEXT | Descrição |
| preco | DECIMAL(10,2) | Preço |
| imagem | TEXT | URL da imagem |

### Pedido
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Chave primária |
| restaurante_id | BIGINT | FK para Restaurante |
| mesa_numero | INT | Número da mesa |
| status | ENUM | EM_ESPERA, EM_ANDAMENTO, CONCLUIDO |
| created_at | DATETIME | Data de criação |
| started_at | DATETIME | Data de início do preparo |

### PedidoProduto
| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Chave primária |
| order_id | BIGINT | FK para Pedido |
| produto_id | BIGINT | FK para Produto |
| observacao | TEXT | Observação do item |
| quantidade | INT | Quantidade |

---

## Comunicação em Tempo Real

### WebSocket
- **Endpoint:** `/ws/pedidos/{restauranteId}`
- **Protocolo:** Jakarta WebSocket nativo
- **Separação:** Cada restaurante tem seu próprio canal

### Fluxo
1. Cliente envia pedido (POST `/pedido`)
2. Servlet processa e salva no banco
3. DAO notifica via `PedidoWebSocket.notificarRestaurante()`
4. Painel recebe em tempo real
5. Notificação sonora no navegador

### Mensagens
- **Novo pedido:** JSON completo do pedido
- **Status update:** `{ "tipo": "STATUS_UPDATE", "pedidoId": X, "novoStatus": "..." }`

---

## Rotas da Aplicação

### Autenticação
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/login` | Login do administrador |
| POST | `/cadastro` | Cadastro de novo restaurante |

### Cardápio (Cliente)
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/cardapio/` | Página do cardápio |
| GET | `/produtos?restauranteId=X` | Lista produtos em JSON |
| POST | `/pedido?restauranteId=X` | Envia novo pedido |

### Painel (Admin)
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/painel/` | Página do painel |
| GET | `/pedidos` | Lista pedidos ativos |
| POST | `/pedido/status` | Atualiza status do pedido |

### Produtos (Admin)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/admin/produto` | Criar produto |
| PUT | `/admin/produto` | Atualizar produto |
| DELETE | `/admin/produto?id=X` | Excluir produto |

### QR Code
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/qrcode?mesa=X&restauranteId=Y` | Gera imagem PNG do QR Code |

---

## Banco de Dados

### Relacionamentos
Usuario (1) ──── (1) Restaurante
│
┌─────────┼─────────┐
(1:N)│ (1:N)│ (1:N)│
│         │     │
RestauranteMesa Produto Pedido
│
(1:N)│
│
PedidoProduto
│
(N:1)│
Produto



---

## MVP (Entrega Final)

- ✅ Login e cadastro de administrador
- ✅ CRUD completo de produtos
- ✅ Cardápio público acessível por QR Code
- ✅ Carrinho de compras no navegador
- ✅ Envio de pedidos com validação de mesa
- ✅ Painel em tempo real com WebSocket
- ✅ Atualização de status de pedidos
- ✅ Geração de QR Code em PNG
- ✅ Interface responsiva
- ✅ Autenticação com hash SHA-256 + Salt
