const API = '/api/auth';

function salvarToken(token) {
    localStorage.setItem('greenday_token', token);
}

// O login da página é processado pelo LoginController em POST /login.
// Assim, quando e-mail ou senha estiverem incorretos, o Spring retorna
// a própria página com a mensagem de erro exibida pelo Thymeleaf.

document.getElementById('form-cadastro')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const nome = document.getElementById('nome').value;
    const email = document.getElementById('email').value;
    const telefone = document.getElementById('telefone').value;
    const senha = document.getElementById('senha').value;
    const confirmarSenha = document.getElementById('confirmarSenha')?.value;

    if (confirmarSenha !== undefined && senha !== confirmarSenha) {
        alert('As senhas nao conferem.');
        return;
    }

    const resp = await fetch(`${API}/cadastro`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nome, email, telefone, senha })
    });

    if (resp.ok) {
        window.location.href = '/login';
    } else {
        const erro = await resp.json();
        alert(erro.mensagem || 'Erro ao cadastrar.');
    }
});
