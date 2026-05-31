<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%-- Painel administrativo do restaurante. --%>
<%-- Aqui o usuário faz login, vê pedidos em tempo real e controla produtos e QR codes. --%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QRFood - Painel do Restaurante</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<!-- ============================================================
     CONTAINER DE AUTENTICAÇÃO (LOGIN / CADASTRO)
     ============================================================ -->
<%-- Zona de login/cadastro que o restaurante vê antes de entrar no painel. --%>
<div class="auth-container" id="auth-container">
    <div class="auth-wrapper">
        
        <!-- CARD DE LOGIN -->
        <div class="auth-card login-card" id="login-card">
            <div class="auth-logo">
    <img src="../imagens/QRFoodLogo.png" 
         alt="QRFood Logo" 
         class="logo-icon-img">
    <h1>QRFood</h1>
    <p>Painel do Restaurante</p>
</div>
            
            <form class="auth-form" onsubmit="event.preventDefault(); login();">
                <div class="form-group">
                    <label for="email">📧 Email</label>
                    <input type="email" id="email" placeholder="seu@email.com" required>
                </div>
                <div class="form-group">
                    <label for="senha">🔒 Senha</label>
                    <input type="password" id="senha" placeholder="••••••••" required>
                </div>
                <button type="submit" class="btn-auth">Entrar</button>
            </form>
            
            <div class="auth-toggle">
                Não tem uma conta? <a onclick="showRegister()">Cadastre-se aqui</a>
            </div>
        </div>
        
        <!-- CARD DE CADASTRO -->
        <div class="auth-card register-card" id="register-card">
            <div class="auth-logo">
    <img src="../imagens/QRFoodLogo.png" 
         alt="QRFood Logo" 
         class="logo-icon-img">
    <h1>Criar Conta</h1>
    <p>Comece a gerenciar seu restaurante</p>
</div>
            
            <form class="auth-form" onsubmit="event.preventDefault(); cadastrar();">
                <div class="form-group">
                    <label for="reg-nome">👤 Nome do Restaurante</label>
                    <input type="text" id="reg-nome" placeholder="Nome do seu restaurante" required>
                </div>
                <div class="form-group">
                    <label for="reg-email">📧 Email</label>
                    <input type="email" id="reg-email" placeholder="seu@email.com" required>
                </div>
                <div class="form-group">
                    <label for="reg-senha">🔒 Senha</label>
                    <input type="password" id="reg-senha" placeholder="Mínimo 6 caracteres" required minlength="6">
                </div>
                <button type="submit" class="btn-auth">Criar Conta</button>
            </form>
            
            <div class="auth-toggle">
                Já tem uma conta? <a onclick="showLogin()">Faça login</a>
            </div>
        </div>
        
    </div>
</div>

<!-- ============================================================
     PAINEL PRINCIPAL (OCULTO ATÉ LOGIN)
     ============================================================ -->
<%-- Conteúdo do painel que só aparece depois que o restaurante faz login. --%>
<main class="container-principal painel-grid hidden" id="painel-principal">
    
    <!-- Cabeçalho do Painel -->
    <section class="painel-card">
        <div class="painel-header">
            <div>
                <h2>📊 Pedidos em Tempo Real</h2>
                <p id="ws-status" class="status-off">Aguardando conexão...</p>
            </div>
            <button class="btn-secondary" onclick="deslogar()">🚪 Sair</button>
        </div>
    </section>
    
    <!-- Lista de Pedidos -->
    <section class="painel-card">
        <div class="painel-section">
            <h3>🛎️ Pedidos Ativos</h3>
            <ul class="orders-list" id="orders-list">
                <li class="empty-state">✨ Nenhum pedido no momento</li>
            </ul>
        </div>
    </section>
    
    <!-- QR Code + Produtos -->
    <div class="painel-section dupla">
        
        <!-- Gerar QR Code -->
        <div class="subcard">
            <h3>📱 Gerar QR Code da Mesa</h3>
            
            <div class="form-group">
                <label for="restaurante-id-display">🏪 Restaurante ID</label>
                <input type="text" id="restaurante-id-display" value="Não logado" readonly>
            </div>
            
            <div class="form-group">
                <label for="mesa-numero">🪑 Número da Mesa</label>
                <input type="number" id="mesa-numero" min="1" value="1">
            </div>
            
            <button class="btn-adicionar" onclick="gerarQRCode()" style="width:100%;">
                ✨ Gerar QR Code
            </button>
            
            <div class="qr-box hidden" id="qr-result">
                <p style="font-weight:600;color:var(--cinza-escuro);">🔗 Link do Cardápio:</p>
                <a id="qr-url" href="#" target="_blank"></a>
                <img id="qr-img" alt="QR Code da Mesa" style="margin-top:16px;">
            </div>
        </div>
        
        <!-- Gerenciar Produtos -->
        <div class="subcard">
            <h3>🍽️ Gerenciar Produtos</h3>
            
            <button class="btn-secondary" onclick="abrirFormProduto()" style="margin-bottom:16px;">
                ➕ Novo Produto
            </button>
            
            <ul id="produtos-list" class="orders-list">
                <li class="empty-state">📦 Carregando produtos...</li>
            </ul>
            
            <!-- Formulário de Produto (oculto) -->
            <div id="form-produto" class="hidden subcard" style="margin-top:16px;background:var(--branco-frio);">
                <h4 id="form-produto-titulo" style="color:var(--roxo-principal);margin-bottom:16px;">Novo Produto</h4>
                
                <input type="hidden" id="prod-id">
                
                <div class="form-group">
                    <label for="prod-nome">📝 Nome do Produto</label>
                    <input type="text" id="prod-nome" placeholder="Ex: X-Burger Especial">
                </div>
                
                <div class="form-group">
                    <label for="prod-desc">📋 Descrição</label>
                    <textarea id="prod-desc" rows="2" placeholder="Descrição do produto..."></textarea>
                </div>
                
                <div class="form-group">
                    <label for="prod-preco">💲 Preço (R$)</label>
                    <input type="number" id="prod-preco" step="0.01" min="0" placeholder="29.90">
                </div>
                
                <div class="form-group">
                    <label for="prod-imagem">🖼️ URL da Imagem</label>
                    <input type="url" id="prod-imagem" placeholder="https://...">
                </div>
                
                <div style="display:flex; gap:12px;">
                    <button class="btn-adicionar" onclick="salvarProduto()" style="flex:1;">💾 Salvar</button>
                    <button class="btn-secondary" onclick="fecharFormProduto()" style="flex:1;">❌ Cancelar</button>
                </div>
            </div>
        </div>
        
    </div>
    
</main>

<!-- Toast de Notificação -->
<div class="toast" id="toast"></div>

<%-- Script do painel: controla login, lista de pedidos, geração de QR codes e CRUD de produtos. --%>
<script src="${pageContext.request.contextPath}/js/painel.js"></script>
</body>
</html>