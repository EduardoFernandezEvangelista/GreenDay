const propriedadeSelecionada = localStorage.getItem('greenday_propriedade_id');

// As páginas de cultura, sensores, clima, alertas, irrigação e relatórios
// precisam saber qual propriedade está selecionada.
document.querySelectorAll('[data-propriedade-link]').forEach(link => {
    if (propriedadeSelecionada) {
        const url = new URL(link.href, window.location.origin);
        url.searchParams.set('propriedadeId', propriedadeSelecionada);
        link.href = url.toString();
    } else {
        link.href = '/propriedades';
        link.title = 'Selecione uma propriedade primeiro';
    }
});

document.getElementById('btn-sair')?.addEventListener('click', (event) => {
    event.preventDefault();
    localStorage.removeItem('greenday_token');
    localStorage.removeItem('greenday_propriedade_id');
    window.location.href = '/logout';
});
