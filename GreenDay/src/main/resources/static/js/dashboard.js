let propriedadeId = obterPropriedadeId();
const headers = cabecalhosAutenticados();
const numero = (valor) => Number.isFinite(Number(valor)) ? Number(valor) : 0;

function atualizarRelogio(){ const el=document.getElementById('relogio-dashboard'); if(el) el.textContent=new Intl.DateTimeFormat('pt-BR',{dateStyle:'medium',timeStyle:'medium'}).format(new Date()); }
atualizarRelogio(); setInterval(atualizarRelogio,1000);

async function carregarCliente(){
  try{const r=await fetch('/api/auth/me',{headers}); if(!r.ok) return; const u=await r.json();
    document.getElementById('nome-cliente').textContent=(u.nome||'produtor').split(' ')[0];
  }catch(e){console.debug('Cliente não identificado',e)}
}

async function selecionarPrimeiraPropriedade(){
  if(propriedadeId) return propriedadeId;
  try{const r=await fetch('/api/propriedades',{headers}); if(!r.ok) return null; const lista=await r.json();
    if(Array.isArray(lista)&&lista.length){ propriedadeId=lista[0].id; localStorage.setItem('greenday_propriedade_id',propriedadeId); return propriedadeId; }
  }catch(e){console.error(e)} return null;
}

async function carregarClimaAoVivo(id, resumo){
  const alvo=document.getElementById('clima-resumo');
  try{
    let clima=resumo?.climaAtual;
    if(!clima){ const r=await fetch(`/api/propriedades/${id}/clima/atual`,{headers}); if(r.ok) clima=await r.json(); }
    if(clima) alvo.textContent=`${Math.round(clima.temperaturaCelsius)}°C • ${clima.descricao||'Condições atuais'}`;
    else alvo.textContent='Cadastre coordenadas para clima ao vivo';
  }catch(e){alvo.textContent='Aguardando dados climáticos'}
}

async function carregarDashboard(){
  const id=await selecionarPrimeiraPropriedade();
  const cards=document.getElementById('cards-resumo');
  if(!id){ cards.innerHTML='<div class="empty-state"><div class="empty-icon">🏡</div><h3>Comece cadastrando sua propriedade</h3><p>Depois disso, o GreenDay passa a centralizar todos os dados do seu cultivo.</p><a class="btn-link" href="/propriedades">Cadastrar propriedade →</a></div>'; return; }
  try{
    const resposta=await fetch(`/api/propriedades/${id}/dashboard`,{headers}); if(!resposta.ok) throw new Error(); const resumo=await resposta.json();
    document.getElementById('nome-propriedade').textContent=resumo.propriedade||'Propriedade ativa';
    document.getElementById('subtitulo-dashboard').textContent=`Acompanhe em tempo real o que está acontecendo em ${resumo.propriedade||'sua propriedade'}.`;
    cards.innerHTML=`<div class="card live-card"><h3>🌱 Culturas</h3><p>${numero(resumo.totalCulturas)}</p><small>Cadastros ativos</small></div><div class="card live-card"><h3>📡 Sensores</h3><p>${numero(resumo.totalSensores)}</p><small>Monitoramento contínuo</small></div><div class="card live-card"><h3>⚠ Alertas não lidos</h3><p>${numero(resumo.alertasNaoLidos)}</p><small>Verificados agora</small></div><div class="card live-card"><h3>💧 Irrigações</h3><p>${numero(resumo.totalIrrigacoes)}</p><small>Registros da propriedade</small></div>`;
    const alertasR=await fetch(`/api/propriedades/${id}/alertas/nao-lidos`,{headers}); const alertas=alertasR.ok?await alertasR.json():[];
    document.getElementById('lista-alertas').innerHTML=Array.isArray(alertas)&&alertas.length?alertas.slice(0,5).map(a=>`<li><b>${a.severidade||'INFO'}</b> • ${a.mensagem||'Novo alerta'}</li>`).join(''):'<li>✓ Nenhum alerta pendente. Tudo sob controle.</li>';
    const alertasN=numero(resumo.alertasNaoLidos), sensores=numero(resumo.totalSensores), irrigacoes=numero(resumo.totalIrrigacoes);
    const saude=Math.max(45,Math.min(100,100-alertasN*8+(sensores?3:0)+(irrigacoes?2:0))); document.getElementById('health-value').textContent=`${saude}%`; document.getElementById('health-label').textContent=saude>=85?'Excelente':saude>=70?'Estável':'Atenção';
    document.getElementById('health-sensors').textContent=`✓ ${sensores} sensor(es) cadastrados`;
    document.getElementById('health-irrigation').textContent=`✓ ${irrigacoes} irrigação(ões) monitorada(s)`;
    document.getElementById('health-alerts').textContent=alertasN?`⚠ ${alertasN} alerta(s) precisam de atenção`:'✓ Sem alertas pendentes';
    await carregarClimaAoVivo(id,resumo);
    document.getElementById('ultima-atualizacao').textContent='Atualizado agora • dados sincronizados';
  }catch(e){ console.error(e); document.getElementById('sync-status').textContent='Reconectando...'; cards.innerHTML='<div class="empty-state"><h3>Reconectando aos dados</h3><p>Vamos tentar novamente automaticamente.</p></div>'; }
}

carregarCliente(); carregarDashboard();
setInterval(carregarDashboard,10000); // atualização em tempo quase real (10 segundos)
setInterval(()=>{ if(propriedadeId) fetch(`/api/propriedades/${propriedadeId}/clima/atualizar`,{method:'POST',headers}).catch(()=>{}); },600000);
document.getElementById('btn-sair')?.addEventListener('click',()=>{localStorage.removeItem('greenday_token');window.location.href='/login';});
