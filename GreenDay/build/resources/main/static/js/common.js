// Contexto compartilhado das páginas do GreenDay.
function obterPropriedadeId() {
    const params = new URLSearchParams(window.location.search);
    let id = params.get('propriedadeId');
    if (id) localStorage.setItem('greenday_propriedade_id', id);
    else id = localStorage.getItem('greenday_propriedade_id');
    return id;
}

function cabecalhosAutenticados(extra = {}) {
    const headers = Object.assign({}, extra);
    const token = localStorage.getItem('greenday_token');
    // O login normal usa cookie HTTP-only. Só envia Bearer se existir token real.
    if (token && token !== 'null' && token !== 'undefined') headers.Authorization = `Bearer ${token}`;
    return headers;
}

function avisarSemPropriedade(elementId) {
    const el = document.getElementById(elementId);
    if (el) el.innerHTML = '<div class="empty-state"><div class="empty-icon">🏡</div><h3>Escolha uma propriedade</h3><p>Para usar esta área, selecione ou cadastre uma propriedade primeiro.</p><a class="btn-link" href="/propriedades">Ir para propriedades →</a></div>';
}

document.addEventListener('DOMContentLoaded', () => {
    const propriedadeId = obterPropriedadeId();
    document.querySelectorAll('nav.navbar a[href]').forEach((link) => {
        const url = new URL(link.getAttribute('href'), window.location.origin);
        if (propriedadeId && url.pathname !== '/propriedades' && url.pathname !== '/usuarios') {
            url.searchParams.set('propriedadeId', propriedadeId);
            link.setAttribute('href', url.pathname + url.search);
        }
    });
});
