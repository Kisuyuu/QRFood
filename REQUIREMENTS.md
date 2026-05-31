# requirements.md

## Projeto

**QRFood** - Sistema de Pedidos via QR Code

## Descrição

Sistema web para restaurantes, lanchonetes e carrinhos de lanche criarem cardápio digital acessado por QR Code. Clientes visualizam produtos e realizam pedidos pelo celular. Os pedidos são enviados em tempo real para o painel do restaurante via WebSocket.

---

## Requisitos Funcionais

### RF01 - Cadastro de usuário administrador
O sistema deve permitir cadastro e login do responsável pelo estabelecimento.
- ✅ Implementado (Servlet `/cadastro` e `/login`)
- Senha com hash SHA-256 + Salt

### RF02 - Gerenciamento de produtos
O administrador poderá cadastrar, editar, remover e visualizar produtos.
- ✅ Implementado (CRUD completo via `/admin/produto`)
- Interface no painel administrativo

### RF03 - Upload de imagem
Cada produto poderá possuir imagem ilustrativa.
- ✅ Implementado (URL da imagem armazenada no banco)
- Exibição no cardápio e no painel

### RF04 - Geração de QR Code
O sistema deverá gerar QR Code para acesso ao cardápio digital.
- ✅ Implementado (Servlet `/qrcode` + ZXing)
- Imagem PNG gerada dinamicamente
- Link: `/cardapio/index.jsp?mesa=X&restauranteId=Y`

### RF05 - Visualização de cardápio
Clientes poderão visualizar produtos e preços.
- ✅ Implementado (JSP + JavaScript)
- Grid responsivo com imagens, descrições e preços

### RF06 - Carrinho de compras
Clientes poderão adicionar itens ao carrinho antes de finalizar pedido.
- ✅ Implementado (carrinho no navegador)
- Quantidade, observações e total em tempo real

### RF07 - Envio de pedido
O cliente poderá enviar pedido ao estabelecimento.
- ✅ Implementado (POST `/pedido`)
- Validação de mesa e restaurante
- Persistência no banco de dados

### RF08 - Atualização de pedidos em tempo real
O sistema deve enviar novos pedidos em tempo real para o painel da cozinha usando WebSocket.
- ✅ Implementado (Jakarta WebSocket)
- Endpoint: `/ws/pedidos/{restauranteId}`
- Notificação sonora no painel

### RF09 - Atualização de status de pedido
O administrador pode alterar o status do pedido.
- ✅ Implementado (Servlet `/pedido/status`)
- Status: `EM_ESPERA` → `EM_ANDAMENTO` → `CONCLUIDO`
- Atualização em tempo real via WebSocket

---

## Requisitos Não Funcionais

### RNF01 - Interface responsiva
Interface responsiva para celular e tablet.
- ✅ Implementado (CSS Grid, Flexbox, Media Queries)

### RNF02 - Tempo de resposta adequado
Tempo de resposta adequado para operações.
- ✅ Implementado (Servlet + JDBC direto, sem overhead)

### RNF03 - Banco de dados relacional
Dados armazenados em banco relacional normalizado.
- ✅ Implementado (MySQL 8, 6 tabelas, Foreign Keys)

### RNF04 - Autenticação segura
Sistema com autenticação básica e senhas protegidas.
- ✅ Implementado (SHA-256 + Salt, AuthFilter, HttpSession)

### RNF05 - Comunicação em tempo real
Comunicação em tempo real utilizando WebSocket.
- ✅ Implementado (Jakarta WebSocket nativo)

### RNF06 - Sistema leve
Sistema deve ser leve e adequado a pequenos negócios.
- ✅ Implementado (Servlet puro, sem Spring, sem frameworks pesados)

---

## Tecnologias Utilizadas

| Camada | Tecnologia |
|--------|------------|
| Backend | Java 21, Servlet (Jakarta EE), JDBC |
| Frontend | JSP, JavaScript Vanilla, CSS3 |
| Banco de Dados | MySQL 8 |
| Servidor | Tomcat 10 |
| Build | Maven (WAR) |
| QR Code | ZXing |
| Tempo Real | Jakarta WebSocket |
| Segurança | SHA-256 + Salt |
| JSON | Gson |

---

## Status dos Requisitos

| Requisito | Status |
|-----------|--------|
| RF01 - Cadastro/Login | ✅ Concluído |
| RF02 - CRUD Produtos | ✅ Concluído |
| RF03 - Imagem do produto | ✅ Concluído |
| RF04 - QR Code | ✅ Concluído |
| RF05 - Cardápio | ✅ Concluído |
| RF06 - Carrinho | ✅ Concluído |
| RF07 - Envio de pedido | ✅ Concluído |
| RF08 - Tempo real | ✅ Concluído |
| RF09 - Status do pedido | ✅ Concluído |
| RNF01 - Responsivo | ✅ Concluído |
| RNF02 - Performance | ✅ Concluído |
| RNF03 - BD Relacional | ✅ Concluído |
| RNF04 - Autenticação | ✅ Concluído |
| RNF05 - WebSocket | ✅ Concluído |
| RNF06 - Leve | ✅ Concluído |

---

## Restrições

- Projeto compatível com prazo acadêmico até **16 de junho de 2026**
- Desenvolvimento individual
- Entrega para disciplina de Programação Orientada a Objetos (FATEC Praia Grande)
