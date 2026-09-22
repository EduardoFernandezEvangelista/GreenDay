package br.com.senai.greenday.controller;

import br.com.senai.greenday.model.Irrigacao;
import br.com.senai.greenday.service.IrrigacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/irrigacoes")
@RequiredArgsConstructor
public class IrrigacaoController {

    private final IrrigacaoService irrigacaoService;

    @PostMapping
    public ResponseEntity<Irrigacao> agendar(@PathVariable Long propriedadeId, @RequestBody Irrigacao irrigacao) {
        return ResponseEntity.ok(irrigacaoService.agendar(propriedadeId, irrigacao));
    }

    @GetMapping
    public ResponseEntity<List<Irrigacao>> listar(@PathVariable Long propriedadeId) {
        return ResponseEntity.ok(irrigacaoService.listarPorPropriedade(propriedadeId));
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<Irrigacao> iniciar(@PathVariable Long propriedadeId, @PathVariable Long id) {
        return ResponseEntity.ok(irrigacaoService.iniciar(id));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Irrigacao> concluir(@PathVariable Long propriedadeId,
                                               @PathVariable Long id,
                                               @RequestBody Map<String, Double> corpo) {
        return ResponseEntity.ok(irrigacaoService.concluir(id, corpo.get("volumeLitros")));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Irrigacao> cancelar(@PathVariable Long propriedadeId, @PathVariable Long id) {
        return ResponseEntity.ok(irrigacaoService.cancelar(id));
    }
}
