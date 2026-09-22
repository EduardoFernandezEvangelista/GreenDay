package br.com.senai.greenday.service;

import br.com.senai.greenday.exception.ResourceNotFoundException;
import br.com.senai.greenday.model.Cultura;
import br.com.senai.greenday.repository.CulturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CulturaService {

    private final CulturaRepository culturaRepository;
    private final PropriedadeService propriedadeService;

    public Cultura criar(Long propriedadeId, Cultura cultura) {
        cultura.setPropriedade(propriedadeService.buscarPorId(propriedadeId));
        return culturaRepository.save(cultura);
    }

    public List<Cultura> listarPorPropriedade(Long propriedadeId) {
        return culturaRepository.findByPropriedadeId(propriedadeId);
    }

    public Cultura buscarPorId(Long id) {
        return culturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cultura nao encontrada: " + id));
    }

    public Cultura atualizar(Long id, Cultura dados) {
        Cultura cultura = buscarPorId(id);
        cultura.setNome(dados.getNome());
        cultura.setVariedade(dados.getVariedade());
        cultura.setDataPlantio(dados.getDataPlantio());
        cultura.setPrevisaoColheita(dados.getPrevisaoColheita());
        cultura.setAreaOcupadaHectares(dados.getAreaOcupadaHectares());
        return culturaRepository.save(cultura);
    }

    public void excluir(Long id) {
        culturaRepository.delete(buscarPorId(id));
    }
}
