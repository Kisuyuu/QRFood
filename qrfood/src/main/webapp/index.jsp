<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QRFood - Cardápio</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <header class="topo-cardapio">
        <div class="logo">🍔 QRFood</div>
        <div class="info-mesa">Mesa: <strong>${param.mesa != null ? param.mesa : 'Não identificada'}</strong></div>
    </header>

    <main class="container-principal">
        <section class="menu-grid">
            
            <c:forEach var="produto" items="${produtos}">
                <article class="produto-card">
                    <img src="${produto.imagem != null ? produto.imagem : 'https://via.placeholder.com/150'}" alt="${produto.nome}" class="produto-img">
                    
                    <div class="produto-info">
                        <h3 class="produto-nome">${produto.nome}</h3>
                        <p class="produto-desc">${produto.descricao}</p>
                        <span class="produto-preco">R$ ${produto.preco}</span>
                    </div>

                    <div class="produto-acao">
                        <input type="text" id="obs-${produto.id}" class="input-obs" placeholder="Ex: Sem cebola, mal passado...">
                        <div class="acao-botoes">
                            <input type="number" id="qtd-${produto.id}" class="input-qtd" value="1" min="1">
                            <button class="btn-adicionar" 
        data-id="${produto.id}" 
        data-nome="${produto.nome}" 
        data-preco="${produto.preco}" 
        onclick="dispararAdicionar(this)">Adicionar</button>
                        </div>
                    </div>
                </article>
            </c:forEach>

            <c:if test="${empty produtos}">
                <article class="produto-card">
                    <img src="https://via.placeholder.com/150" alt="Hambúrguer Fake" class="produto-img">
                    <div class="produto-info">
                        <h3 class="produto-nome">Hambúrguer Clássico (Teste)</h3>
                        <p class="produto-desc">Pão brioche, carne 160g, queijo e bacon.</p>
                        <span class="produto-preco">R$ 25.90</span>
                    </div>
                    <div class="produto-acao">
                        <input type="text" class="input-obs" placeholder="Ex: Sem cebola, mal passado...">
                        <div class="acao-botoes">
                            <input type="number" class="input-qtd" value="1" min="1">
                            <button class="btn-adicionar">Adicionar</button>
                        </div>
                    </div>
                </article>
            </c:if>
            </section>
    </main>

    <aside class="carrinho-flutuante" id="carrinho">
        <div class="carrinho-resumo">
            <span id="itens-qtd">0 itens</span>
            <span id="total-preco">Total: R$ 0,00</span>
        </div>
        <button class="btn-finalizar" onclick="enviarPedido()">Enviar Pedido</button>
    </aside>

    <script src="js/cardapio.js"></script>
</body>
</html>