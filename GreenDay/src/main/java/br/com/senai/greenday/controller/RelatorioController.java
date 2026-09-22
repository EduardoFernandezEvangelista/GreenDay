package br.com.senai.greenday.controller;

import br.com.senai.greenday.model.Recomendacao;
import br.com.senai.greenday.service.RecomendacaoService;
import br.com.senai.greenday.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final RecomendacaoService recomendacaoService;

    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Object>> resumo(@PathVariable Long propriedadeId) {
        return ResponseEntity.ok(relatorioService.gerarResumoPropriedade(propriedadeId));
    }

    @GetMapping("/recomendacoes")
    public ResponseEntity<List<Recomendacao>> recomendacoes(@PathVariable Long propriedadeId) {
        return ResponseEntity.ok(recomendacaoService.gerarParaPropriedade(propriedadeId));
    }
}
