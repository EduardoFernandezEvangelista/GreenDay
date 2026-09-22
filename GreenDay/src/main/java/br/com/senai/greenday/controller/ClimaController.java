package br.com.senai.greenday.controller;

import br.com.senai.greenday.dto.ClimaDTO;
import br.com.senai.greenday.model.Clima;
import br.com.senai.greenday.model.Propriedade;
import br.com.senai.greenday.service.ApiMeteorologicaService;
import br.com.senai.greenday.service.ClimaService;
import br.com.senai.greenday.service.PropriedadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propriedades/{propriedadeId}/clima")
@RequiredArgsConstructor
public class ClimaController {

    private final ClimaService climaService;
    private final ApiMeteorologicaService apiMeteorologicaService;
    private final PropriedadeService propriedadeService;

    @GetMapping("/atual")
    public ResponseEntity<ClimaDTO> atual(@PathVariable Long propriedadeId) {
        return ResponseEntity.ok(ClimaDTO.fromEntity(climaService.ultimo(propriedadeId)));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<ClimaDTO>> historico(@PathVariable Long propriedadeId) {
        List<ClimaDTO> historico = climaService.historico(propriedadeId).stream()
                .map(ClimaDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(historico);
    }

    /** Forca uma nova consulta a API meteorologica externa para essa propriedade. */
    @PostMapping("/atualizar")
    public ResponseEntity<ClimaDTO> atualizarViaApiExterna(@PathVariable Long propriedadeId) {
        Propriedade propriedade = propriedadeService.buscarPorId(propriedadeId);
        Clima clima = apiMeteorologicaService.buscarEArmazenarClimaAtual(propriedade);
        return ResponseEntity.ok(ClimaDTO.fromEntity(clima));
    }
}
