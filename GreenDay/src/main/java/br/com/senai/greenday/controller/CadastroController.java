package br.com.senai.greenday.controller;

import br.com.senai.greenday.model.Role;
import br.com.senai.greenday.model.Usuario;
import br.com.senai.greenday.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CadastroController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String realizarCadastro(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            @RequestParam(required = false) String telefone,
            Model model) {

        if (!senha.equals(confirmarSenha)) {

            model.addAttribute(
                    "erro",
                    "As senhas não conferem"
            );

            return "cadastro";
        }

        if (senha.length() < 6) {

            model.addAttribute(
                    "erro",
                    "A senha deve ter no mínimo 6 caracteres"
            );

            return "cadastro";
        }

        if (usuarioRepository.existsByEmail(email)) {

            model.addAttribute(
                    "erro",
                    "Este e-mail já está cadastrado"
            );

            return "cadastro";
        }

        Usuario novoUsuario = new Usuario();

        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setTelefone(telefone);

        // Criptografa a senha
        novoUsuario.setSenha(
                passwordEncoder.encode(senha)
        );

        // Cadastro público sempre será PRODUTOR
        novoUsuario.setRole(Role.PRODUTOR);

        usuarioRepository.save(novoUsuario);

        model.addAttribute(
                "sucesso",
                "Cadastro realizado com sucesso! Agora faça login."
        );

        return "cadastro";
    }
}