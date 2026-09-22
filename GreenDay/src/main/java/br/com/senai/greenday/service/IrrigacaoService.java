package br.com.senai.greenday.service;

import br.com.senai.greenday.exception.ResourceNotFoundException;
import br.com.senai.greenday.model.Irrigacao;
import br.com.senai.greenday.model.Propriedade;
import br.com.senai.greenday.repository.IrrigacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IrrigacaoService {

    private final IrrigacaoRepository irrigacaoRepository;
    private final PropriedadeService propriedadeService;

    public Irrigacao agendar(Long propriedadeId, Irrigacao irrigacao) {
        Propriedade propriedade = propriedadeService.buscarPorId(propriedadeId);
        irrigacao.setPropriedade(propriedade);
        irrigacao.setStatus(Irrigacao.Status.AGENDADA);
        return irrigacaoRepository.save(irrigacao);
    }

    public List<Irrigacao> listarPorPropriedade(Long propriedadeId) {
        return irrigacaoRepository.findByPropriedadeIdOrderByInicioDesc(propriedadeId);
    }

    public Irrigacao iniciar(Long id) {
        Irrigacao irrigacao = buscarPorId(id);
        irrigacao.setStatus(Irrigacao.Status.EM_ANDAMENTO);
        irrigacao.setInicio(LocalDateTime.now());
        return irrigacaoRepository.save(irrigacao);
    }

    public Irrigacao concluir(Long id, Double volumeLitros) {
        Irrigacao irrigacao = buscarPorId(id);
        irrigacao.setStatus(Irrigacao.Status.CONCLUIDA);
        irrigacao.setFim(LocalDateTime.now());
        irrigacao.setVolumeLitros(volumeLitros);
        return irrigacaoRepository.save(irrigacao);
    }

    public Irrigacao cancelar(Long id) {
        Irrigacao irrigacao = buscarPorId(id);
        irrigacao.setStatus(Irrigacao.Status.CANCELADA);
        return irrigacaoRepository.save(irrigacao);
    }

    private Irrigacao buscarPorId(Long id) {
        return irrigacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Irrigacao nao encontrada: " + id));
    }
}
