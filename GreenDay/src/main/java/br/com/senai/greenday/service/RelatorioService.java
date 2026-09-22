package br.com.senai.greenday.service;

import br.com.senai.greenday.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final PropriedadeService propriedadeService;
    private final CulturaService culturaService;
    private final SensorService sensorService;
    private final ClimaService climaService;
    private final AlertaService alertaService;
    private final IrrigacaoService irrigacaoService;

    /** Monta um resumo consolidado da propriedade para relatorios/dashboard. */
    public Map<String, Object> gerarResumoPropriedade(Long propriedadeId) {
        Propriedade propriedade = propriedadeService.buscarPorId(propriedadeId);
        List<Cultura> culturas = culturaService.listarPorPropriedade(propriedadeId);
        List<Sensor> sensores = sensorService.listarPorPropriedade(propriedadeId);
        List<Alerta> alertasNaoLidos = alertaService.listarNaoLidos(propriedadeId);
        List<Irrigacao> irrigacoes = irrigacaoService.listarPorPropriedade(propriedadeId);

        Clima climaAtual = null;
        try {
            climaAtual = climaService.ultimo(propriedadeId);
        } catch (Exception ignored) {
            // sem dados climaticos ainda
        }

        Map<String, Object> resumo = new LinkedHashMap<>();
        resumo.put("propriedade", propriedade.getNome());
        resumo.put("totalCulturas", culturas.size());
        resumo.put("totalSensores", sensores.size());
        resumo.put("alertasNaoLidos", alertasNaoLidos.size());
        resumo.put("totalIrrigacoes", irrigacoes.size());
        resumo.put("climaAtual", climaAtual);
        resumo.put("atualizadoEm", java.time.LocalDateTime.now().toString());
        return resumo;
    }
}
