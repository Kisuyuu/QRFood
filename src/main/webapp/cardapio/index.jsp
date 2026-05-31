<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%-- Página do cardápio digital. Aqui o JSP monta a estrutura da tela e chama o CSS/JS do cardápio. --%>
<%-- O número da mesa vem pela URL e o conteúdo do menu é carregado dinamicamente no navegador. --%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QRFood - Cardápio Digital</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%-- Cabeçalho fixo do cardápio, mostra a marca e o número da mesa atual. --%>
<header class="topo-cardapio">
    <div class="logo">
    <img src="../imagens/QRFoodLogo.png" 
         alt="QRFood" 
         style="width: 40px; height: 40px; border-radius: 10px; vertical-align: middle; margin-right: 8px;">
    QRFood
</div>
    <div class="info-mesa">
        🪑 Mesa <strong>${param.mesa != null ? param.mesa : '?'}</strong>
    </div>
</header>

<%-- Área principal onde o JavaScript vai montar os cards dos produtos. --%>
<main class="container-principal">
    <section class="menu-grid" id="menu-grid">
        <!-- Produtos serão carregados dinamicamente -->
        <div style="text-align:center;grid-column:1/-1;padding:60px;color:var(--cinza-medio);">
            <span style="font-size:48px;">🍽️</span>
            <p style="margin-top:16px;">Carregando cardápio...</p>
        </div>
    </section>
</main>

<%-- Carrinho flutuante que aparece quando o usuário adiciona produtos. --%>
<aside class="carrinho-flutuante hidden" id="carrinho">
    <div class="carrinho-resumo">
        <span id="itens-qtd">🛒 0 itens</span>
        <span id="total-preco">Total: R$ 0,00</span>
    </div>
    <button class="btn-finalizar" onclick="enviarPedido()">📤 Enviar Pedido</button>
</aside>

<div class="toast" id="toast"></div>
<%-- Script do cardápio: busca produtos, controla carrinho e envia o pedido ao servidor. --%>
<script src="${pageContext.request.contextPath}/js/cardapio.js"></script>
</body>
</html>