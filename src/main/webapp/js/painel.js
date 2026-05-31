// ─── Estado global ────────────────────────────────────────────────────────────
let restauranteId = null;
const contextPath = window.location.pathname.split('/').slice(0, 2).join('/');
const wsProtocol  = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
let socket;
let pedidos  = [];
let produtos = [];
let editandoProduto = null;

// ─── Utilitários ──────────────────────────────────────────────────────────────

function setVisible(id, visible) {
  const el = document.getElementById(id);
  if (el) el.classList.toggle('hidden', !visible);
}

function showToast(msg, success = true) {
  const t = document.getElementById('toast');
  if (!t) return;
  t.textContent = msg;
  t.className = success ? 'toast toast-success' : 'toast toast-error';
  t.style.display = 'block';
  setTimeout(() => (t.style.display = 'none'), 2800);
}

function fmtHora(iso) {
  if (!iso) return '–';
  try {
    return new Date(iso).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  } catch (_) { return '–'; }
}

function fmtPreco(v) {
  return Number(v).toFixed(2).replace('.', ',');
}

function atualizarDisplayRestaurante() {
  const display = document.getElementById('restaurante-id-display');
  if (display) {
    display.value = restauranteId ? `Restaurante #${restauranteId}` : 'Não logado';
  }
}

// ─── Transições Login/Cadastro ────────────────────────────────────────────────

function showRegister() {
  const loginCard = document.getElementById('login-card');
  const registerCard = document.getElementById('register-card');
  if (loginCard && registerCard) {
    loginCard.classList.add('slide-out');
    registerCard.classList.add('slide-in');
  }
}

function showLogin() {
  const loginCard = document.getElementById('login-card');
  const registerCard = document.getElementById('register-card');
  if (loginCard && registerCard) {
    loginCard.classList.remove('slide-out');
    registerCard.classList.remove('slide-in');
  }
}

// ─── Cadastro de Novo Restaurante ─────────────────────────────────────────────

async function cadastrar() {
  const nome  = document.getElementById('reg-nome')?.value.trim();
  const email = document.getElementById('reg-email')?.value.trim();
  const senha = document.getElementById('reg-senha')?.value.trim();
  
  if (!nome || !email || !senha) {
    showToast('Preencha todos os campos.', false);
    return;
  }
  
  if (senha.length < 6) {
    showToast('A senha deve ter no mínimo 6 caracteres.', false);
    return;
  }
  
  try {
    const resp = await fetch(`${contextPath}/cadastro`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ nome, email, senha })
    });
    
    const data = await resp.json();
    
    if (!resp.ok) {
      throw new Error(data.erro || 'Erro ao cadastrar');
    }
    
    showToast('✅ Conta criada com sucesso! Faça login.');
    showLogin();
    
    // Preenche o email no campo de login
    const emailInput = document.getElementById('email');
    if (emailInput) {
      emailInput.value = email;
    }
    
    // Limpa e foca no campo de senha
    const senhaInput = document.getElementById('senha');
    if (senhaInput) {
      senhaInput.value = '';
      senhaInput.focus();
    }
    
  } catch (e) {
    showToast(e.message || 'Erro ao criar conta. Tente novamente.', false);
  }
}

// ─── Login / Logout ───────────────────────────────────────────────────────────

async function login() {
  const email = document.getElementById('email')?.value.trim();
  const senha = document.getElementById('senha')?.value.trim();
  if (!email || !senha) { showToast('Preencha email e senha.', false); return; }

  try {
    const resp = await fetch(`${contextPath}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, senha })
    });
    const data = await resp.json();
    if (!resp.ok) { showToast(data.erro || 'Erro no login', false); return; }

    restauranteId = data.restauranteId;
    atualizarDisplayRestaurante();

    // Esconde a tela de autenticação e mostra o painel
    const authContainer = document.getElementById('auth-container');
    if (authContainer) {
      authContainer.style.display = 'none';
    }
    setVisible('painel-principal', true);

    connectWebSocket();
    await carregarPedidosExistentes();
    await carregarProdutos();

    showToast('✅ Login realizado com sucesso!');
  } catch (_) {
    showToast('Erro ao conectar ao servidor.', false);
  }
}

function deslogar() {
  if (socket) socket.close();
  pedidos = []; 
  produtos = []; 
  restauranteId = null;
  
  // Mostra a tela de autenticação novamente
  const authContainer = document.getElementById('auth-container');
  if (authContainer) {
    authContainer.style.display = '';
    // Reseta os cards para estado inicial
    showLogin();
  }
  
  setVisible('painel-principal', false);
  
  // Limpa campos de login
  const emailInput = document.getElementById('email');
  const senhaInput = document.getElementById('senha');
  if (emailInput) emailInput.value = '';
  if (senhaInput) senhaInput.value = '';
  
  showToast('Logout realizado.');
}

// ─── WebSocket ────────────────────────────────────────────────────────────────

function connectWebSocket() {
  const statusEl = document.getElementById('ws-status');
  if (!statusEl) return;
  
  try {
    const url = `${wsProtocol}//${window.location.host}${contextPath}/ws/pedidos/${restauranteId}`;
    socket = new WebSocket(url);

    socket.onopen = () => {
      statusEl.textContent = '🟢 Conectado em tempo real';
      statusEl.className   = 'status-on';
    };
    socket.onmessage = ({ data }) => {
      try {
        const msg = JSON.parse(data);
        if (msg.tipo === 'STATUS_UPDATE') {
          aplicarStatusUpdate(msg.pedidoId, msg.novoStatus);
        } else {
          receberNovoPedido(msg);
        }
      } catch (e) { console.error('[WS] parse error', e); }
    };
    socket.onclose = () => {
      statusEl.textContent = '🔴 WebSocket desconectado';
      statusEl.className   = 'status-off';
    };
    socket.onerror = () => {
      statusEl.textContent = '⚠️ Erro no WebSocket';
      statusEl.className   = 'status-off';
    };
  } catch (_) {
    statusEl.textContent = '⚠️ Não foi possível conectar';
    statusEl.className   = 'status-off';
  }
}

// ─── Pedidos ──────────────────────────────────────────────────────────────────

async function carregarPedidosExistentes() {
  try {
    const resp = await fetch(`${contextPath}/pedidos`);
    if (!resp.ok) return;
    pedidos = await resp.json();
    renderPedidos();
  } catch (e) {
    console.error('Erro ao carregar pedidos:', e);
  }
}

function receberNovoPedido(pedido) {
  pedido.id     = pedido.id || Date.now();
  pedido.status = pedido.status || 'EM_ESPERA';
  const idx = pedidos.findIndex(p => p.id === pedido.id);
  if (idx >= 0) pedidos[idx] = pedido;
  else          pedidos.unshift(pedido);
  renderPedidos();
  playBeep();
}

function aplicarStatusUpdate(pedidoId, novoStatus) {
  if (novoStatus === 'CONCLUIDO') {
    pedidos = pedidos.filter(p => p.id !== pedidoId);
  } else {
    const p = pedidos.find(p => p.id === pedidoId);
    if (p) p.status = novoStatus;
  }
  renderPedidos();
}

function renderPedidos() {
  const list = document.getElementById('orders-list');
  if (!list) return;
  
  if (!pedidos.length) {
    list.innerHTML = '<li class="empty-state">✨ Nenhum pedido no momento</li>';
    return;
  }

  list.innerHTML = pedidos.map(p => {
    const status = p.status || 'EM_ESPERA';

    const itensHtml = (p.itens || []).map(item => `
      <div class="order-item">
        <strong>${item.quantidade}× ${item.nomeProduto || 'Produto #' + item.produtoId}</strong>
        ${item.observacao ? `<p class="obs">📝 ${item.observacao}</p>` : ''}
      </div>`).join('') || '<p class="empty-state">Sem itens</p>';

    const btns = status !== 'CONCLUIDO' ? `
      <div class="order-actions">
        ${status === 'EM_ESPERA'
          ? `<button class="btn-status btn-andamento"
               onclick="mudarStatus(${p.id},'EM_ANDAMENTO')">▶ Em andamento</button>`
          : ''}
        <button class="btn-status btn-concluir"
                onclick="mudarStatus(${p.id},'CONCLUIDO')">✔ Concluir</button>
      </div>` : '';

    return `
      <li class="order-card order-${status.toLowerCase()}">
        <header>
          <div class="order-header-info">
            <strong>🪑 Mesa ${p.mesaNumero || '–'}</strong>
            <span class="order-time">🕐 ${fmtHora(p.createdAt)}</span>
          </div>
          <span class="badge ${status.toLowerCase()}">${status.replace('_', ' ')}</span>
        </header>
        <div class="order-body">${itensHtml}</div>
        ${btns}
      </li>`;
  }).join('');
}

async function mudarStatus(pedidoId, novoStatus) {
  try {
    const resp = await fetch(`${contextPath}/pedido/status`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ pedidoId, status: novoStatus })
    });
    if (!resp.ok) throw new Error();
    aplicarStatusUpdate(pedidoId, novoStatus);
    showToast(`Status: ${novoStatus.replace('_', ' ')}`);
  } catch (_) {
    showToast('Erro ao atualizar status.', false);
  }
}

// ─── QR Code ──────────────────────────────────────────────────────────────────

function gerarQRCode() {
  const numero = document.getElementById('mesa-numero')?.value.trim();
  if (!numero)         { showToast('Informe o número da mesa.', false); return; }
  if (!restauranteId)  { showToast('Faça login primeiro.',     false); return; }

  const pageUrl  = `${window.location.origin}${contextPath}/cardapio/index.jsp?mesa=${numero}&restauranteId=${restauranteId}`;
  const imgSrc   = `${contextPath}/qrcode?mesa=${numero}&restauranteId=${restauranteId}`;

  const qrUrlEl = document.getElementById('qr-url');
  if (qrUrlEl) {
    qrUrlEl.textContent = pageUrl;
    qrUrlEl.href        = pageUrl;
  }
  
  // Cria/atualiza a tag <img> para exibir o QR Code
  const qrResultDiv = document.getElementById('qr-result');
  if (!qrResultDiv) return;
  
  let img = document.getElementById('qr-img');
  if (!img) {
    img = document.createElement('img');
    img.id = 'qr-img';
    img.alt = 'QR Code para mesa ' + numero;
    img.style.maxWidth = '200px';
    img.style.display = 'block';
    img.style.marginTop = '10px';
    qrResultDiv.appendChild(img);
  }
  img.src = imgSrc;

  qrResultDiv.classList.remove('hidden');
  showToast('QR Code gerado com sucesso!');
}

// ─── Produtos (CRUD) ──────────────────────────────────────────────────────────

async function carregarProdutos() {
  try {
    const resp = await fetch(`${contextPath}/produtos?restauranteId=${restauranteId}`);
    if (!resp.ok) throw new Error('Falha ao carregar');
    produtos = await resp.json();
    renderProdutos();
  } catch (e) { 
    console.error('Erro ao carregar produtos:', e);
    const list = document.getElementById('produtos-list');
    if (list) list.innerHTML = '<li class="empty-state">Erro ao carregar produtos.</li>';
  }
}

function renderProdutos() {
  const list = document.getElementById('produtos-list');
  if (!list) return;
  
  if (!produtos || produtos.length === 0) {
    list.innerHTML = '<li class="empty-state">📦 Nenhum produto cadastrado.</li>';
    return;
  }
  
  list.innerHTML = produtos.map(p => `
    <li class="order-card" style="flex-direction:row; align-items:center; justify-content:space-between;">
      <div class="produto-admin-info" style="flex-grow:1;">
        ${p.imagem
          ? `<img src="${p.imagem}" style="width:50px;height:50px;object-fit:cover;border-radius:10px;margin-right:12px;" alt="${p.nome}" onerror="this.style.display='none'">`
          : '<div style="width:50px;height:50px;background:var(--roxo-claro);border-radius:10px;margin-right:12px;text-align:center;line-height:50px;font-size:20px;">🍽️</div>'}
        <div>
          <strong>${p.nome}</strong>
          ${p.descricao ? `<p class="order-meta">${p.descricao}</p>` : ''}
          <span style="font-weight:700;color:var(--roxo-principal);">R$ ${fmtPreco(p.preco)}</span>
        </div>
      </div>
      <div style="display:flex; gap:8px;">
        <button class="btn-secondary" onclick="abrirEdicao(${p.id})" style="padding:8px 12px;">✏️</button>
        <button class="btn-danger" onclick="excluirProduto(${p.id})">🗑️</button>
      </div>
    </li>`).join('');
}

function abrirFormProduto(produto = null) {
  editandoProduto = produto;
  
  const titulo = document.getElementById('form-produto-titulo');
  if (titulo) titulo.textContent = produto ? '✏️ Editar Produto' : '➕ Novo Produto';
  
  const idInput = document.getElementById('prod-id');
  if (idInput) idInput.value = produto?.id || '';
  
  const nomeInput = document.getElementById('prod-nome');
  if (nomeInput) nomeInput.value = produto?.nome || '';
  
  const descInput = document.getElementById('prod-desc');
  if (descInput) descInput.value = produto?.descricao || '';
  
  const precoInput = document.getElementById('prod-preco');
  if (precoInput) precoInput.value = produto?.preco || '';
  
  const imgInput = document.getElementById('prod-imagem');
  if (imgInput) imgInput.value = produto?.imagem || '';
  
  setVisible('form-produto', true);
  
  // Foca no campo nome
  if (nomeInput) nomeInput.focus();
}

function abrirEdicao(id) {
  const produto = produtos.find(p => p.id === id);
  if (produto) abrirFormProduto(produto);
  else showToast('Produto não encontrado.', false);
}

function fecharFormProduto() {
  editandoProduto = null;
  setVisible('form-produto', false);
}

async function salvarProduto() {
  const nome   = document.getElementById('prod-nome')?.value.trim();
  const desc   = document.getElementById('prod-desc')?.value.trim();
  const preco  = parseFloat(document.getElementById('prod-preco')?.value);
  const imagem = document.getElementById('prod-imagem')?.value.trim();
  const idRaw  = document.getElementById('prod-id')?.value;

  if (!nome || isNaN(preco) || preco <= 0) {
    showToast('Nome e um preço válido são obrigatórios.', false);
    return;
  }

  const payload = { nome, descricao: desc, preco, imagem, restauranteId };
  if (idRaw) payload.id = parseInt(idRaw, 10);

  const isUpdate = !!idRaw;
  const url = `${contextPath}/admin/produto`;
  const method = isUpdate ? 'PUT' : 'POST';

  try {
    const resp = await fetch(url, {
      method: method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (!resp.ok) {
      const err = await resp.json();
      throw new Error(err.error || 'Erro ao salvar');
    }
    fecharFormProduto();
    await carregarProdutos();
    showToast(isUpdate ? '✅ Produto atualizado!' : '✅ Produto criado com sucesso!');
  } catch (e) {
    showToast(e.message || 'Erro ao salvar produto.', false);
  }
}

async function excluirProduto(id) {
  if (!confirm('Tem certeza que deseja excluir este produto? Esta ação não pode ser desfeita.')) return;
  try {
    const resp = await fetch(`${contextPath}/admin/produto?id=${id}`, { method: 'DELETE' });
    if (!resp.ok) {
        const err = await resp.json();
        throw new Error(err.error || 'Erro ao excluir');
    }
    await carregarProdutos();
    showToast('🗑️ Produto excluído com sucesso.');
  } catch (e) {
    showToast(e.message || 'Erro ao excluir produto.', false);
  }
}

// ─── Som de notificação ───────────────────────────────────────────────────────

function playBeep() {
  try {
    const ctx  = new (window.AudioContext || window.webkitAudioContext)();
    const osc  = ctx.createOscillator();
    const gain = ctx.createGain();
    osc.connect(gain);
    gain.connect(ctx.destination);
    osc.frequency.value = 880;
    gain.gain.setValueAtTime(0.25, ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.35);
    osc.start(ctx.currentTime);
    osc.stop(ctx.currentTime + 0.35);
  } catch (_) {}
}

// ─── Init ─────────────────────────────────────────────────────────────────────

window.addEventListener('DOMContentLoaded', () => {
  setVisible('painel-principal', false);
  
  const wsStatus = document.getElementById('ws-status');
  if (wsStatus) wsStatus.textContent = 'Aguardando login...';

  // Enter no campo senha dispara login
  const senhaInput = document.getElementById('senha');
  if (senhaInput) {
    senhaInput.addEventListener('keydown', e => { 
      if (e.key === 'Enter') login(); 
    });
  }
  
  // Enter no campo de senha do cadastro dispara cadastro
  const regSenhaInput = document.getElementById('reg-senha');
  if (regSenhaInput) {
    regSenhaInput.addEventListener('keydown', e => {
      if (e.key === 'Enter') cadastrar();
    });
  }
});