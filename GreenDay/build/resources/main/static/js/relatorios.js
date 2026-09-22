const propriedadeId = obterPropriedadeId();
const headers = cabecalhosAutenticados({ 'Content-Type': 'application/json' });

async function carregarRelatorios() {
    const resumo = await fetch(`/api/propriedades/${propriedadeId}/relatorios/resumo`, { headers }).then(r => r.json());
    document.getElementById('resumo-propriedade').innerHTML = `
        <p><strong>${resumo.propriedade}</strong></p>
        <p>Culturas: ${resumo.totalCulturas} | Sensores: ${resumo.totalSensores}</p>
        <p>Alertas nao lidos: ${resumo.alertasNaoLidos} | Irrigacoes: ${resumo.totalIrrigacoes}</p>
    `;

    const recomendacoes = await fetch(`/api/propriedades/${propriedadeId}/relatorios/recomendacoes`, { headers }).then(r => r.json());
    document.getElementById('lista-recomendacoes').innerHTML =
        recomendacoes.map(r => `<li>[${r.categoria}] ${r.texto}</li>`).join('') || '<li>Nenhuma recomendacao no momento.</li>';
}

if (propriedadeId) carregarRelatorios(); else avisarSemPropriedade('resumo-propriedade');
