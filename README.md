# QRFood

Sistema de cardápio digital via QR Code com pedidos em tempo real, desenvolvido para a disciplina de Programação Orientada a Objetos da Fatec Praia Grande.

---

## Objetivo

Permitir que estabelecimentos alimentícios disponibilizem seus cardápios digitalmente e recebam pedidos em tempo real diretamente no painel da cozinha.

---

## Funcionalidades

* Cadastro e login de administrador
* Cadastro de produtos com imagens
* Geração de QR Code para acesso ao cardápio
* Visualização de cardápio pelo cliente
* Carrinho de compras
* Envio de pedidos
* Painel da cozinha em tempo real
* Atualização de status de pedidos

---

## Comunicação em tempo real

O sistema utiliza **WebSocket com Spring Boot (STOMP)** para enviar novos pedidos instantaneamente ao painel da cozinha, sem necessidade de atualização manual da página.

---

## Tecnologias

* Java
* Spring Boot
* Spring MVC
* Spring WebSocket
* Thymeleaf
* MySQL
* HTML/CSS/JavaScript
* Bootstrap

---

## Público-alvo

* Lanchonetes
* Restaurantes
* Carrinhos de lanche
* Pequenos negócios alimentícios

---

## Arquitetura

O sistema combina:

* API REST para operações padrão
* WebSocket para comunicação em tempo real
* Renderização server-side com Thymeleaf

---

## Status

Em desenvolvimento

---

## Integrante

* Projeto individual

---

## Entrega

16 de junho

