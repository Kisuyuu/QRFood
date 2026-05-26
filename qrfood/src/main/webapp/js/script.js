function dispararAdicionar(botao) {
    // Puxa os atributos de dados que o JSP injetou no HTML
    const id = botao.getAttribute('data-id');
    const nome = botao.getAttribute('data-nome');
    const preco = parseFloat(botao.getAttribute('data-preco'));

    // Pega a quantidade e observação baseadas no ID do produto
    const qtd = parseInt(document.getElementById(`qtd-${id}`).value) || 1;
    const obs = document.getElementById(`obs-${id}`).value;

    // Agora você pode chamar sua lógica de carrinho numa boa!
    adicionarAoCarrinho(id, nome, preco, qtd, obs);
}

function adicionarAoCarrinho(id, nome, preco, qtd, obs) {
    console.log(`Adicionado: ${qtd}x ${nome} (${obs}) - R$ ${preco * qtd}`);
    // Sua lógica de atualizar o carrinho flutuante entra aqui...
}