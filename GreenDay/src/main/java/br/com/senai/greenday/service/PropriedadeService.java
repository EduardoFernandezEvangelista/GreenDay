package br.com.senai.greenday.service;

import br.com.senai.greenday.exception.ResourceNotFoundException;
import br.com.senai.greenday.model.Propriedade;
import br.com.senai.greenday.model.Usuario;
import br.com.senai.greenday.repository.PropriedadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropriedadeService {
    private final PropriedadeRepository propriedadeRepository;

    public Propriedade criar(Propriedade propriedade, Usuario proprietario) {
        if (proprietario == null) throw new IllegalStateException("Usuário não autenticado.");
        propriedade.setId(null);
        propriedade.setProprietario(proprietario);
        return propriedadeRepository.save(propriedade);
    }

    public List<Propriedade> listarPorUsuario(Long usuarioId) {
        return propriedadeRepository.findByProprietarioId(usuarioId);
    }

    public Propriedade buscarPorId(Long id) {
        return propriedadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriedade não encontrada: " + id));
    }

    public Propriedade buscarDoUsuario(Long id, Usuario usuario) {
        Propriedade propriedade = buscarPorId(id);
        if (usuario == null || propriedade.getProprietario() == null ||
                !propriedade.getProprietario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Propriedade não encontrada.");
        }
        return propriedade;
    }

    public Propriedade atualizar(Long id, Propriedade dados, Usuario usuario) {
        Propriedade p = buscarDoUsuario(id, usuario);
        p.setNome(dados.getNome());
        p.setEndereco(dados.getEndereco());
        p.setCidade(dados.getCidade());
        p.setEstado(dados.getEstado());
        p.setDescricao(dados.getDescricao());
        p.setAreaHectares(dados.getAreaHectares());
        p.setLatitude(dados.getLatitude());
        p.setLongitude(dados.getLongitude());
        p.setAltitude(dados.getAltitude());
        return propriedadeRepository.save(p);
    }

    public void excluir(Long id, Usuario usuario) {
        propriedadeRepository.delete(buscarDoUsuario(id, usuario));
    }
}
