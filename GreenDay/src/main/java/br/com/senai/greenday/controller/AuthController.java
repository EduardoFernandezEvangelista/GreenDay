package br.com.senai.greenday.controller;

import br.com.senai.greenday.dto.CadastroDTO;
import br.com.senai.greenday.dto.LoginDTO;
import br.com.senai.greenday.dto.UsuarioDTO;
import br.com.senai.greenday.model.Usuario;
import br.com.senai.greenday.service.AuthService;
import br.com.senai.greenday.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    /** Retorna os dados do cliente autenticado para personalizar o painel. */
    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> me(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(UsuarioDTO.fromEntity(usuario));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioDTO> cadastrar(@Valid @RequestBody CadastroDTO dto) {
        Usuario usuario = usuarioService.cadastrar(dto);
        return ResponseEntity.ok(UsuarioDTO.fromEntity(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginDTO dto) {
        String token = authService.autenticar(dto);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login-telefone")
    public ResponseEntity<Map<String, String>> loginTelefone(@RequestBody Map<String, String> dados) {
        String telefone = dados.get("telefone");
        String senha = dados.get("senha");
        if (telefone == null || telefone.isBlank() || senha == null || senha.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Informe telefone e senha."));
        }
        try {
            String token = authService.autenticarPorTelefone(telefone, senha);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("erro", "Telefone ou senha incorretos."));
        }
    }
}
