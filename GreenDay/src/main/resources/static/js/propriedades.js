const API_PROPRIEDADES = '/api/propriedades';
let propriedadesCache = [];

const $ = (s) => document.querySelector(s);
const escapeHtml = (v) => String(v ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
function showToast(msg, type='success') {
  const t=$('#toast'); t.textContent=msg; t.className=`toast ${type}`; setTimeout(()=>t.className='toast hidden', 3200);
}
function headersJson(){ return cabecalhosAutenticados({'Content-Type':'application/json'}); }
async function request(url, options={}) {
  const r=await fetch(url, options);
  if(!r.ok){ let m='Não foi possível concluir a operação.'; try{const j=await r.json();m=j.mensagem||j.message||m;}catch{} throw new Error(m); }
  return r.status===204?null:r.json();
}
function abrirModal(p=null){
  const modal=$('#modal-propriedade'); $('#form-propriedade').reset();
  $('#propriedade-id').value=p?.id||'';
  $('#modal-tipo').textContent=p?'EDITAR PROPRIEDADE':'NOVA PROPRIEDADE';
  $('#modal-titulo').textContent=p?'Editar propriedade':'Cadastrar propriedade';
  $('#prop-nome').value=p?.nome||''; $('#prop-area').value=p?.areaHectares??'';
  $('#prop-estado').value=p?.estado||''; $('#prop-cidade').value=p?.cidade||'';
  $('#prop-endereco').value=p?.endereco||''; $('#prop-descricao').value=p?.descricao||'';
  $('#prop-latitude').value=p?.latitude??''; $('#prop-longitude').value=p?.longitude??''; $('#prop-altitude').value=p?.altitude??''; $('#location-status').className='location-status hidden';
  modal.classList.remove('hidden'); modal.setAttribute('aria-hidden','false'); setTimeout(()=>$('#prop-nome').focus(),80);
}
function fecharModal(){ $('#modal-propriedade').classList.add('hidden'); $('#modal-propriedade').setAttribute('aria-hidden','true'); }
function atualizarStats(lista){
  $('#stat-total').textContent=lista.length;
  const area=lista.reduce((s,p)=>s+(Number(p.areaHectares)||0),0); $('#stat-area').textContent=`${area.toLocaleString('pt-BR',{maximumFractionDigits:2})} ha`;
  $('#stat-cidades').textContent=new Set(lista.map(p=>(p.cidade||p.estado||'').trim()).filter(Boolean)).size;
  $('#contador-propriedades').textContent=`${lista.length} ${lista.length===1?'propriedade':'propriedades'}`;
}
function card(p){
 const selecionada=String(localStorage.getItem('greenday_propriedade_id'))===String(p.id);
 const local=[p.cidade,p.estado].filter(Boolean).join(' · ') || p.endereco || 'Localização não informada';
 return `<article class="property-card ${selecionada?'selected':''}">
  <div class="property-card-top"><div class="property-icon">🏡</div><span class="property-status">${selecionada?'● EM USO':'PROPRIEDADE'}</span></div>
  <h2>${escapeHtml(p.nome)}</h2><p class="property-location">📍 ${escapeHtml(local)}</p>
  <div class="property-metrics"><div><b>${p.areaHectares??'—'}</b><span>hectares</span></div><div><b>${p.cidade?escapeHtml(p.cidade):'—'}</b><span>cidade</span></div></div>
  ${p.descricao?`<p class="property-description">${escapeHtml(p.descricao)}</p>`:''}
  <div class="property-actions">
   <button class="btn-open" data-open="${p.id}">Abrir gestão →</button>
   <button class="icon-btn" title="Editar" data-edit="${p.id}">✎</button>
   <button class="icon-btn danger" title="Excluir" data-delete="${p.id}">⌫</button>
  </div></article>`;
}
function render(lista=propriedadesCache){
 const grid=$('#lista-propriedades'); const vazio=$('#vazio-propriedades');
 grid.innerHTML=lista.map(card).join('');
 vazio.classList.toggle('hidden', propriedadesCache.length!==0);
 if(propriedadesCache.length!==0 && lista.length===0) grid.innerHTML='<div class="empty-search">Nenhuma propriedade encontrada para esta busca.</div>';
 atualizarStats(propriedadesCache);
}
async function carregarPropriedades(){
 $('#loading-propriedades').classList.remove('hidden');
 try{ propriedadesCache=await request(API_PROPRIEDADES,{headers:cabecalhosAutenticados()}); render(); }
 catch(e){ $('#lista-propriedades').innerHTML=`<div class="empty-search">⚠️ ${escapeHtml(e.message)}<br><small>Faça login novamente e tente de novo.</small></div>`; }
 finally{$('#loading-propriedades').classList.add('hidden');}
}

function setLocationStatus(message, type='info'){
  const box=$('#location-status'); if(!box) return;
  box.textContent=message; box.className=`location-status ${type}`;
}
async function preencherEnderecoPorCoordenadas(latitude, longitude){
  try{
    setLocationStatus('🌍 Localização encontrada. Identificando cidade e endereço...', 'loading');
    const url=`https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${encodeURIComponent(latitude)}&lon=${encodeURIComponent(longitude)}&zoom=18&addressdetails=1`;
    const r=await fetch(url,{headers:{'Accept':'application/json'}});
    if(!r.ok) throw new Error('Não foi possível identificar o endereço.');
    const data=await r.json(); const a=data.address||{};
    const cidade=a.city||a.town||a.village||a.municipality||a.county||'';
    const estado=a.state||'';
    const endereco=[a.road,a.house_number].filter(Boolean).join(', ') || data.display_name || '';
    if(cidade && !$('#prop-cidade').value.trim()) $('#prop-cidade').value=cidade;
    if(estado && !$('#prop-estado').value.trim()) $('#prop-estado').value=estado;
    if(endereco && !$('#prop-endereco').value.trim()) $('#prop-endereco').value=endereco;
    setLocationStatus(`✓ Localização aplicada com precisão aproximada. ${cidade ? cidade + (estado ? ' · '+estado : '') : ''}`, 'success');
  }catch(err){
    setLocationStatus('✓ Coordenadas preenchidas. Não foi possível buscar o endereço automaticamente; você pode preenchê-lo manualmente.', 'warning');
  }
}
function usarMinhaLocalizacao(){
  if(!navigator.geolocation){ setLocationStatus('⚠️ Seu navegador não suporta geolocalização.', 'error'); return; }
  const btn=$('#btn-usar-localizacao'); const original=btn.textContent;
  btn.disabled=true; btn.textContent='Localizando...';
  setLocationStatus('📍 Solicitando permissão e obtendo sua localização...', 'loading');
  navigator.geolocation.getCurrentPosition(async position=>{
    const {latitude,longitude,altitude,accuracy}=position.coords;
    $('#prop-latitude').value=Number(latitude).toFixed(6);
    $('#prop-longitude').value=Number(longitude).toFixed(6);
    if(altitude!==null && Number.isFinite(altitude)) $('#prop-altitude').value=Number(altitude).toFixed(1);
    $('#coordinates-box').open=true;
    setLocationStatus(`✓ Coordenadas capturadas com precisão aproximada de ${Math.round(accuracy)} metros.`, 'success');
    btn.disabled=false; btn.textContent=original;
    await preencherEnderecoPorCoordenadas(latitude,longitude);
  }, error=>{
    const messages={1:'⚠️ Permissão de localização negada. Você pode preencher os campos manualmente.',2:'⚠️ Não foi possível determinar sua localização. Verifique o GPS/conexão.',3:'⚠️ A localização demorou demais. Tente novamente.'};
    setLocationStatus(messages[error.code]||'⚠️ Não foi possível obter sua localização.', 'error');
    btn.disabled=false; btn.textContent=original;
  },{enableHighAccuracy:true,timeout:15000,maximumAge:30000});
}
$('#btn-usar-localizacao')?.addEventListener('click',usarMinhaLocalizacao);

$('#btn-nova-propriedade')?.addEventListener('click',()=>abrirModal());
$('#btn-primeira-propriedade')?.addEventListener('click',()=>abrirModal());
document.querySelectorAll('[data-close]').forEach(b=>b.addEventListener('click',fecharModal));
$('#modal-propriedade')?.addEventListener('click',e=>{if(e.target.id==='modal-propriedade')fecharModal();});
$('#form-propriedade')?.addEventListener('submit',async e=>{
 e.preventDefault(); const id=$('#propriedade-id').value;
 const num=(id)=>{const v=$(id).value.trim();return v===''?null:Number(v);};
 const dados={nome:$('#prop-nome').value.trim(),areaHectares:num('#prop-area'),estado:$('#prop-estado').value.trim()||null,cidade:$('#prop-cidade').value.trim()||null,endereco:$('#prop-endereco').value.trim()||null,descricao:$('#prop-descricao').value.trim()||null,latitude:num('#prop-latitude'),longitude:num('#prop-longitude'),altitude:num('#prop-altitude')};
 const btn=$('#btn-salvar-propriedade'); btn.disabled=true; btn.textContent='Salvando...';
 try{const salva=await request(id?`${API_PROPRIEDADES}/${id}`:API_PROPRIEDADES,{method:id?'PUT':'POST',headers:headersJson(),body:JSON.stringify(dados)});
   if(!localStorage.getItem('greenday_propriedade_id')) localStorage.setItem('greenday_propriedade_id',salva.id);
   fecharModal(); showToast(id?'Propriedade atualizada com sucesso!':'Propriedade cadastrada! Agora você pode começar a gestão.'); await carregarPropriedades();
 }catch(err){showToast(err.message,'error');}finally{btn.disabled=false;btn.textContent='Salvar propriedade 🌿';}
});
$('#busca-propriedade')?.addEventListener('input',e=>{const q=e.target.value.toLowerCase().trim();render(propriedadesCache.filter(p=>[p.nome,p.cidade,p.estado,p.endereco].some(v=>(v||'').toLowerCase().includes(q))));});
$('#lista-propriedades')?.addEventListener('click',async e=>{
 const b=e.target.closest('button'); if(!b)return; const id=b.dataset.open||b.dataset.edit||b.dataset.delete; const p=propriedadesCache.find(x=>String(x.id)===String(id)); if(!p)return;
 if(b.dataset.open){localStorage.setItem('greenday_propriedade_id',p.id); window.location.href=`/dashboard?propriedadeId=${p.id}`;}
 if(b.dataset.edit)abrirModal(p);
 if(b.dataset.delete){if(!confirm(`Excluir "${p.nome}"? Esta ação também pode remover dados vinculados.`))return;try{await request(`${API_PROPRIEDADES}/${p.id}`,{method:'DELETE',headers:cabecalhosAutenticados()});if(String(localStorage.getItem('greenday_propriedade_id'))===String(p.id))localStorage.removeItem('greenday_propriedade_id');showToast('Propriedade excluída.');await carregarPropriedades();}catch(err){showToast(err.message,'error');}}
});
carregarPropriedades();
