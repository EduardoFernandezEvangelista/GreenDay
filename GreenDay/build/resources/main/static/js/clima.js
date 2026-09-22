const propriedadeId = obterPropriedadeId();
const headers = cabecalhosAutenticados({ 'Content-Type': 'application/json' });

async function carregarClima() {
    const atual = await fetch(`/api/propriedades/${propriedadeId}/clima/atual`, { headers })
        .then(r => r.ok ? r.json() : null);

    document.getElementById('clima-atual').innerHTML = atual
        ? `<p>${atual.temperaturaCelsius}°C - ${atual.descricao ?? ''}</p>
           <p>Umidade: ${atual.umidadeRelativa}% | Vento: ${atual.velocidadeVentoKmh ?? '-'} km/h</p>`
        : '<p>Sem dados climaticos ainda.</p>';

    const historico = await fetch(`/api/propriedades/${propriedadeId}/clima/historico`, { headers }).then(r => r.json());
    document.getElementById('lista-clima-historico').innerHTML =
        historico.map(c => `<li>${c.coletadoEm}: ${c.temperaturaCelsius}°C, ${c.umidadeRelativa}%</li>`).join('');
}

document.getElementById('btn-atualizar-clima')?.addEventListener('click', async () => {
    await fetch(`/api/propriedades/${propriedadeId}/clima/atualizar`, { method: 'POST', headers });
    carregarClima();
});

if (propriedadeId) carregarClima(); else avisarSemPropriedade('clima-atual');
