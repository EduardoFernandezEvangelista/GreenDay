package br.com.senai.greenday.controller;

import br.com.senai.greenday.model.Usuario;
import br.com.senai.greenday.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class RecuperacaoSenhaController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, TokenRecuperacao> tokens = new ConcurrentHashMap<>();

    @GetMapping("/esqueci-senha")
    public String paginaEsqueciSenha() {
        return "esqueci-senha";
    }

    @PostMapping("/esqueci-senha")
    public String solicitar(@RequestParam String email, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
        if (usuario == null) {
            model.addAttribute("erro", "Não encontramos uma conta com esse e-mail.");
            return "esqueci-senha";
        }

        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenRecuperacao(usuario.getEmail(), System.currentTimeMillis() + 15 * 60 * 1000L));

        // Para o projeto local, o link é exibido na tela.
        // Em produção, este link deve ser enviado por e-mail.
        model.addAttribute("sucesso", "Link de recuperação gerado com sucesso.");
        model.addAttribute("linkRecuperacao", "/redefinir-senha?token=" + token);
        return "esqueci-senha";
    }

    @GetMapping("/redefinir-senha")
    public String redefinirPagina(@RequestParam String token, Model model) {
        if (!tokenValido(token)) {
            model.addAttribute("erro", "Link de recuperação inválido ou expirado.");
            return "esqueci-senha";
        }
        model.addAttribute("token", token);
        return "redefinir-senha";
    }

    private boolean tokenValido(String token) {
        TokenRecuperacao t = tokens.get(token);
        if (t == null) return false;
        if (t.expiraEm() < System.currentTimeMillis()) {
            tokens.remove(token);
            return false;
        }
        return true;
    }

    private record TokenRecuperacao(String email, long expiraEm) {}

    @PostMapping("/redefinir-senha")
    public String redefinir(@RequestParam String token,
                            @RequestParam String senha,
                            @RequestParam String confirmarSenha,
                            Model model) {
        String email = tokenValido(token) ? tokens.get(token).email() : null;
        if (email == null) {
            model.addAttribute("erro", "Link de recuperação inválido ou expirado.");
            return "esqueci-senha";
        }
        if (senha.length() < 6) {
            model.addAttribute("erro", "A senha deve ter pelo menos 6 caracteres.");
            model.addAttribute("token", token);
            return "redefinir-senha";
        }
        if (!senha.equals(confirmarSenha)) {
            model.addAttribute("erro", "As senhas não conferem.");
            model.addAttribute("token", token);
            return "redefinir-senha";
        }

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            model.addAttribute("erro", "Usuário não encontrado.");
            return "esqueci-senha";
        }

        usuario.setSenha(passwordEncoder.encode(senha));
        usuarioRepository.save(usuario);
        tokens.remove(token);
        model.addAttribute("sucesso", "Senha alterada com sucesso! Agora você pode entrar.");
        return "index";
    }
}
