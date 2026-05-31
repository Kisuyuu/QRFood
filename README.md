# 🍔 QRFood

Sistema de cardápio digital via QR Code com pedidos em tempo real, desenvolvido para a disciplina de Programação Orientada a Objetos da Fatec Praia Grande.

---

## 🎯 Objetivo

Permitir que estabelecimentos alimentícios disponibilizem seus cardápios digitalmente e recebam pedidos em tempo real diretamente no painel da cozinha.

---

## ✨ Funcionalidades

- 🔐 Cadastro e login de administrador (SHA-256 + Salt)
- 📝 CRUD completo de produtos com imagens
- 📱 Geração de QR Code para acesso ao cardápio
- 🍽️ Visualização de cardápio pelo cliente
- 🛒 Carrinho de compras interativo
- 📤 Envio de pedidos via JSON
- 📊 Painel da cozinha em tempo real
- 🔄 Atualização de status de pedidos (EM_ESPERA → EM_ANDAMENTO → CONCLUIDO)
- 🔊 Notificação sonora de novos pedidos
- 🎨 Interface moderna com tema roxo

---

## 📡 Comunicação em tempo real

O sistema utiliza **WebSocket nativo (Jakarta WebSocket)** para enviar novos pedidos instantaneamente ao painel da cozinha, sem necessidade de atualização manual da página.

Cada restaurante possui seu próprio canal WebSocket separado por `restauranteId`.

---

## 🛠️ Tecnologias

### Backend
- Java 21
- Servlet (Jakarta EE)
- JDBC (MySQL Connector 8)
- Tomcat 10
- Maven (WAR)

### Frontend
- JSP (Jakarta Server Pages)
- JavaScript (Vanilla)
- CSS3 (variáveis, grid, flexbox, animações)

### Banco de Dados
- MySQL 8
- Schema normalizado (6 tabelas)
- Relacionamentos com Foreign Keys

### Segurança
- SHA-256 + Salt para senhas
- Filtro de autenticação (AuthFilter)
- Sessão via HttpSession

### QR Code
- ZXing para geração de imagem PNG

---

## 🗄️ Estrutura do Banco de Dados

| Tabela | Função |
|--------|--------|
| **Usuario** | Dados de login (email, senha hash) |
| **Restaurante** | Vinculado ao usuário |
| **RestauranteMesa** | Mesas para QR Code |
| **Produto** | Itens do cardápio |
| **Pedido** | Pedidos dos clientes |
| **PedidoProduto** | Itens de cada pedido |

---

## 📂 Estrutura do Projeto

qrfood/
├── pom.xml
├── schema.sql
├── AGENTS.md
├── README.md
└── src/main/
├── java/com/qrfood/
│ ├── dao/
│ │ ├── PedidoDAO.java
│ │ ├── ProdutoDAO.java
│ │ ├── RestauranteDAO.java
│ │ ├── RestauranteMesaDAO.java
│ │ └── UsuarioDAO.java
│ ├── filter/
│ │ ├── AuthFilter.java
│ │ └── CacheControlFilter.java
│ ├── model/
│ │ ├── Pedido.java
│ │ ├── PedidoProduto.java
│ │ ├── Produto.java
│ │ ├── Restaurante.java
│ │ ├── RestauranteMesa.java
│ │ ├── StatusPedido.java
│ │ └── Usuario.java
│ ├── servlet/
│ │ ├── CadastroServlet.java
│ │ ├── LoginServlet.java
│ │ ├── PedidoListServlet.java
│ │ ├── PedidoServlet.java
│ │ ├── PedidoStatusServlet.java
│ │ ├── ProdutoAdminServlet.java
│ │ ├── ProdutoServlet.java
│ │ └── QRCodeServlet.java
│ ├── util/
│ │ ├── ConexaoBD.java
│ │ ├── GsonFactory.java
│ │ ├── QRCodeUtil.java
│ │ └── SenhaUtil.java
│ └── websocket/
│ └── PedidoWebSocket.java
└── webapp/
├── css/
│ └── style.css
├── js/
│ ├── cardapio.js
│ └── painel.js
├── imagens/
│ └── QRFoodLogo.png
├── cardapio/
│ └── index.jsp
├── painel/
│ └── index.jsp
├── WEB-INF/
│ └── web.xml
└── index.jsp


---

## 🚀 Como executar

### Pré-requisitos
- Java 21
- Maven
- Tomcat 10
- MySQL 8

### Passos

1. Clone o repositório
```bash
git clone https://github.com/Kisuyuu/QRFood.git

2. Execute o script SQL no MySQL

3. Compile o projeto
"mvn clean package"

4. Copie o WAR para o Tomcat
(pasta target)

5. Inicie o Tomcat e acesse:

Painel: http://localhost:8082/qrfood/painel/

Cardápio: http://localhost:8082/qrfood/cardapio/

🎯 Público-alvo
Lanchonetes

Restaurantes

Carrinhos de lanche

Pequenos negócios alimentícios

🏗️ Arquitetura
text
CLIENTE (WEB)
     ↓
Cardápio (JSP + JS)
     ↓
POST /pedido
     ↓
SERVLET (Java)
     ↓
DAO (MySQL)
     ↓
BANCO DE DADOS
     ↓
WEB SOCKET
     ↓
PAINEL DO RESTAURANTE (tempo real)
🎨 Paleta de Cores
Cor	Hex
Roxo Principal	#6D28D9
Roxo Escuro	#4C1D95
Roxo Claro	#A78BFA
Preto Suave	#0F0F10
Branco Frio	#F5F6FA
📊 Status
✅ Em desenvolvimento - MVP funcional

👨‍💻 Integrante
Kisuyuu - Projeto individual

---
