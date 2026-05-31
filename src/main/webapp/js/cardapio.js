const mesa = getQueryParam('mesa') || '1';
const restauranteId = getQueryParam('restauranteId') || '1';
const contextPath = window.location.pathname.split('/').slice(0,2).join('/');
const pedidoUrl = `${window.location.origin}${contextPath}/pedido?restauranteId=${restauranteId}`;

let produtos = [];

let carrinho = [];

function getQueryParam(name) {
  const params = new URLSearchParams(window.location.search);
  return params.get(name);
}

function formatMoney(value) {
  return value.toFixed(2).replace('.', ',');
}

function showToast(message, sucesso = true) {
  const toast = document.getElementById('toast');
  toast.textContent = message;
  toast.className = sucesso ? 'toast toast-success' : 'toast toast-error';
  toast.style.display = 'block';
  setTimeout(() => toast.style.display = 'none', 2500);
}

function renderProdutos() {
  const container = document.getElementById('menu-grid');
  container.innerHTML = produtos.map(produto => `
    <article class="produto-card">
      <img src="${produto.imagem}" alt="${produto.nome}" class="produto-img">
      <div class="produto-info">
        <h3 class="produto-nome">${produto.nome}</h3>
        <p class="produto-desc">${produto.descricao}</p>
        <span class="produto-preco">R$ ${formatMoney(produto.preco)}</span>
      </div>
      <div class="produto-acao">
        <input type="text" id="obs-${produto.id}" class="input-obs" placeholder="Ex: Sem cebola, mal passado...">
        <div class="acao-botoes">
          <input type="number" id="qtd-${produto.id}" class="input-qtd" value="1" min="1">
          <button class="btn-adicionar" onclick="adicionarAoCarrinho(${produto.id})">Adicionar</button>
        </div>
      </div>
    </article>
  `).join('');
}

function adicionarAoCarrinho(id) {
  const produto = produtos.find(item => item.id === id);
  const qtd = parseInt(document.getElementById(`qtd-${id}`).value, 10) || 1;
  const obs = document.getElementById(`obs-${id}`).value.trim();
  const itemExistente = carrinho.find(item => item.id === id && item.observacao === obs);

  if (itemExistente) {
    itemExistente.quantidade += qtd;
  } else {
    carrinho.push({
      id: produto.id,
      produtoId: produto.id,
      nome: produto.nome,
      quantidade: qtd,
      observacao: obs,
      preco: produto.preco
    });
  }

  atualizarCarrinho();
  showToast(`Adicionado ${qtd}x ${produto.nome}`);
}

function atualizarCarrinho() {
  const carrinhoEl = document.getElementById('carrinho');
  if (carrinho.length === 0) {
    carrinhoEl.classList.add('hidden');
    return;
  }

  carrinhoEl.classList.remove('hidden');
  const total = carrinho.reduce((sum, item) => sum + item.preco * item.quantidade, 0);
  document.getElementById('itens-qtd').textContent = `${carrinho.length} itens`;
  document.getElementById('total-preco').textContent = `Total: R$ ${formatMoney(total)}`;
}

function enviarPedido() {
  if (carrinho.length === 0) {
    showToast('Adicione pelo menos um item ao pedido.', false);
    return;
  }

  const mesaId = parseInt(mesa, 10) || 0;
  const payload = {
    mesaNumero: mesaId,
    status: 'EM_ESPERA',
    itens: carrinho.map(item => ({ produtoId: item.produtoId, quantidade: item.quantidade, observacao: item.observacao }))
  };

  fetch(pedidoUrl, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  })
    .then(async response => {
      if (!response.ok) throw new Error('Erro na requisição');
      return response.json();
    })
    .then(data => {
      if (data.id) {
        showToast('Pedido enviado com sucesso!');
        carrinho = [];
        atualizarCarrinho();
      } else {
        showToast('Erro ao enviar pedido.', false);
      }
    })
    .catch(() => showToast('Não foi possível conectar ao servidor.', false));
}

document.addEventListener(
    'DOMContentLoaded',
    () => {

        carregarProdutos();

        atualizarCarrinho();
    }
);

 async function carregarProdutos() {

    try {

        const response =
            await fetch(
                `${contextPath}/produtos?restauranteId=${restauranteId}`
            );

        produtos = await response.json();

        renderProdutos();

    } catch (e) {

        showToast(
            'Erro ao carregar produtos',
            false
        );

    }
}
