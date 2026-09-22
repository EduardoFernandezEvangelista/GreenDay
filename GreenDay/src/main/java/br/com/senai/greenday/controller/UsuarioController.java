package br.com.senai.greenday.controller;

import br.com.senai.greenday.dto.CadastroDTO;
import br.com.senai.greenday.dto.UsuarioDTO;
import br.com.senai.greenday.model.Usuario;
import br.com.senai.greenday.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioDTO> meuPerfil(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(UsuarioDTO.fromEntity(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioDTO> atualizarPerfil(@AuthenticationPrincipal Usuario usuario,
                                                        @Valid @RequestBody CadastroDTO dto) {
        Usuario atualizado = usuarioService.atualizar(usuario.getId(), dto);
        return ResponseEntity.ok(UsuarioDTO.fromEntity(atualizado));
    }

    @GetMapping("/admin/todos")
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos().stream()
                .map(UsuarioDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        usuarioService.ativar(id);
        return ResponseEntity.noContent().build();
    }
}
