package br.com.senai.greenday.controller;

import br.com.senai.greenday.model.Cultura;
import br.com.senai.greenday.service.CulturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/culturas")
@RequiredArgsConstructor
public class CulturaController {

    private final CulturaService culturaService;

    @PostMapping
    public ResponseEntity<Cultura> criar(@PathVariable Long propriedadeId, @RequestBody Cultura cultura) {
        return ResponseEntity.ok(culturaService.criar(propriedadeId, cultura));
    }

    @GetMapping
    public ResponseEntity<List<Cultura>> listar(@PathVariable Long propriedadeId) {
        return ResponseEntity.ok(culturaService.listarPorPropriedade(propriedadeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cultura> atualizar(@PathVariable Long propriedadeId,
                                              @PathVariable Long id,
                                              @RequestBody Cultura dados) {
        return ResponseEntity.ok(culturaService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long propriedadeId, @PathVariable Long id) {
        culturaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
