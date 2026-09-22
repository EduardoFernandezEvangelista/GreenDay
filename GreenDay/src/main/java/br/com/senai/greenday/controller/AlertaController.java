package br.com.senai.greenday.controller;

import br.com.senai.greenday.model.Alerta;
import br.com.senai.greenday.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    public ResponseEntity<List<Alerta>> listar(@PathVariable Long propriedadeId) {
        return ResponseEntity.ok(alertaService.listarPorPropriedade(propriedadeId));
    }

    @GetMapping("/nao-lidos")
    public ResponseEntity<List<Alerta>> naoLidos(@PathVariable Long propriedadeId) {
        return ResponseEntity.ok(alertaService.listarNaoLidos(propriedadeId));
    }

    @PatchMapping("/{id}/marcar-lido")
    public ResponseEntity<Alerta> marcarLido(@PathVariable Long propriedadeId, @PathVariable Long id) {
        return ResponseEntity.ok(alertaService.marcarComoLido(id));
    }
}
