const propriedadeId = obterPropriedadeId();
const headers = cabecalhosAutenticados({ 'Content-Type': 'application/json' });

async function carregarAlertas() {
    const alertas = await fetch(`/api/propriedades/${propriedadeId}/alertas`, { headers }).then(r => r.json());
    document.getElementById('lista-alertas-completa').innerHTML = alertas.map(a => `
        <li>
            [${a.severidade}] ${a.tipo}: ${a.mensagem}
            ${a.lido ? '' : `<button onclick="marcarLido(${a.id})">Marcar como lido</button>`}
        </li>`).join('');
}

async function marcarLido(id) {
    await fetch(`/api/propriedades/${propriedadeId}/alertas/${id}/marcar-lido`, { method: 'PATCH', headers });
    carregarAlertas();
}

if (propriedadeId) carregarAlertas(); else avisarSemPropriedade('lista-alertas-completa');
