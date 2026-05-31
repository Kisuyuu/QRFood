# AGENTS.md

## QRFood - Sistema de Pedidos via QR Code

### Projeto individual

Desenvolvedor único responsável por todas as camadas do sistema.

---

## Stack Tecnológica

### Backend
- Java 21
- Servlet (Jakarta EE)
- JDBC (MySQL Connector)
- Tomcat 10
- Maven (WAR)

### Frontend
- JSP (Jakarta Server Pages)
- JavaScript (Vanilla)
- CSS3 (variáveis, grid, animações)

### Banco de Dados
- MySQL 8
- Schema normalizado (6 tabelas)
- Relacionamentos com FK e constraints

### Tempo Real
- WebSocket nativo (Jakarta WebSocket)
- Notificação de novos pedidos
- Separação por restauranteId

### Segurança
- SHA-256 + Salt (SenhaUtil)
- Filtro de autenticação (AuthFilter)
- Sessão via HttpSession

### QR Code
- ZXing (geração de imagem PNG)
- Endpoint dedicado `/qrcode`

---

## Responsabilidades

### Backend
- Servlets para endpoints REST
- DAOs para acesso a dados
- WebSocket para comunicação em tempo real
- Regras de negócio no servidor

### Frontend
- Cardápio digital para clientes (QR Code)
- Painel administrativo do restaurante
- Interface de login/cadastro com transições
- Carrinho de compras no navegador

### Banco de Dados
- Modelagem do schema (6 tabelas)
- Relacionamentos: Usuario → Restaurante → Mesas, Produtos, Pedidos
- Persistência via JDBC

### Tempo Real
- Integração WebSocket no painel
- Notificação instantânea de novos pedidos
- Atualização de status em tempo real

### Documentação
- README com instruções de instalação
- Schema SQL do banco de dados
- Estrutura do projeto documentada

---

## Estrutura do Projeto

qrfood/
├── pom.xml
├── schema.sql
├── AGENTS.md
├── README.md
└── src/main/
├── java/com/qrfood/
│ ├── dao/ (acesso a dados)
│ ├── filter/ (filtros de segurança)
│ ├── model/ (entidades)
│ ├── servlet/ (controladores)
│ ├── util/ (utilitários)
│ └── websocket/ (tempo real)
└── webapp/
├── css/
├── js/
├── imagens/
├── cardapio/ (cliente)
├── painel/ (admin)
└── WEB-INF/


---

## Banco de Dados

| Tabela | Função |
|--------|--------|
| Usuario | Dados de login |
| Restaurante | Vinculado ao usuário |
| RestauranteMesa | Mesas para QR Code |
| Produto | Itens do cardápio |
| Pedido | Pedidos dos clientes |
| PedidoProduto | Itens de cada pedido |
