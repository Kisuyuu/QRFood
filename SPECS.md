# SPECS.md

## Tecnologias

* Java 17
* Spring Boot
* Spring MVC
* Thymeleaf
* MySQL
* HTML5
* CSS3
* JavaScript
* Bootstrap

## Arquitetura

* Controller
* Service
* Repository
* Model

## Entidades Principais

### Usuario

* id
* nome
* email
* senha

### Produto

* id
* nome
* descricao
* preco
* imagem
* categoria

### Pedido

* id
* cliente
* itens
* valorTotal
* status

## Rotas Principais

### Administrador

* /login
* /dashboard
* /produtos
* /pedidos

### Cliente

* /menu/{loja}
* /carrinho
* /checkout

## Banco de Dados

Tabelas:

* usuarios
* produtos
* pedidos
* pedido_itens

## MVP (Entrega Inicial)

* Login admin
* CRUD produtos
* Cardápio público
* QR Code
* Pedido simples

