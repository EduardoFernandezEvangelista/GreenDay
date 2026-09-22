package br.com.senai.greenday.controller;

import br.com.senai.greenday.model.Propriedade;
import br.com.senai.greenday.model.Usuario;
import br.com.senai.greenday.service.PropriedadeService;
import br.com.senai.greenday.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/propriedades")
@RequiredArgsConstructor
public class PropriedadeController {
    private final PropriedadeService propriedadeService;
    private final RelatorioService relatorioService;

    @PostMapping
    public ResponseEntity<Propriedade> criar(@AuthenticationPrincipal Usuario usuario, @RequestBody Propriedade propriedade) {
        return ResponseEntity.ok(propriedadeService.criar(propriedade, usuario));
    }

    @GetMapping
    public ResponseEntity<List<Propriedade>> minhasPropriedades(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(propriedadeService.listarPorUsuario(usuario.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Propriedade> buscar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(propriedadeService.buscarDoUsuario(id, usuario));
    }

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        propriedadeService.buscarDoUsuario(id, usuario);
        return ResponseEntity.ok(relatorioService.gerarResumoPropriedade(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Propriedade> atualizar(@PathVariable Long id, @RequestBody Propriedade dados,
                                                  @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(propriedadeService.atualizar(id, dados, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        propriedadeService.excluir(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
