const propriedadeId = obterPropriedadeId();
const headers = cabecalhosAutenticados({ 'Content-Type': 'application/json' });

async function carregarIrrigacoes() {
    const irrigacoes = await fetch(`/api/propriedades/${propriedadeId}/irrigacoes`, { headers }).then(r => r.json());
    const tbody = document.querySelector('#tabela-irrigacoes tbody');
    tbody.innerHTML = irrigacoes.map(i => `
        <tr>
            <td>${i.inicio ?? '-'}</td><td>${i.fim ?? '-'}</td>
            <td>${i.volumeLitros ?? '-'}</td><td>${i.status}</td>
            <td>
                ${i.status === 'AGENDADA' ? `<button onclick="iniciar(${i.id})">Iniciar</button>` : ''}
                ${i.status === 'EM_ANDAMENTO' ? `<button onclick="concluir(${i.id})">Concluir</button>` : ''}
                ${(i.status === 'AGENDADA' || i.status === 'EM_ANDAMENTO') ? `<button onclick="cancelar(${i.id})">Cancelar</button>` : ''}
            </td>
        </tr>`).join('');
}

async function iniciar(id) {
    await fetch(`/api/propriedades/${propriedadeId}/irrigacoes/${id}/iniciar`, { method: 'PATCH', headers });
    carregarIrrigacoes();
}

async function concluir(id) {
    const volumeLitros = Number(prompt('Volume utilizado (litros):', '0'));
    await fetch(`/api/propriedades/${propriedadeId}/irrigacoes/${id}/concluir`, {
        method: 'PATCH', headers, body: JSON.stringify({ volumeLitros })
    });
    carregarIrrigacoes();
}

async function cancelar(id) {
    if (!confirm('Cancelar esta irrigacao?')) return;
    await fetch(`/api/propriedades/${propriedadeId}/irrigacoes/${id}/cancelar`, { method: 'PATCH', headers });
    carregarIrrigacoes();
}

document.getElementById('btn-agendar-irrigacao')?.addEventListener('click', async () => {
    await fetch(`/api/propriedades/${propriedadeId}/irrigacoes`, { method: 'POST', headers, body: JSON.stringify({}) });
    carregarIrrigacoes();
});

if (propriedadeId) carregarIrrigacoes(); else avisarSemPropriedade('tabela-irrigacoes');
