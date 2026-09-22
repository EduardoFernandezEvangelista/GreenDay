package br.com.senai.greenday.repository;

import br.com.senai.greenday.model.Irrigacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IrrigacaoRepository extends JpaRepository<Irrigacao, Long> {
    List<Irrigacao> findByPropriedadeIdOrderByInicioDesc(Long propriedadeId);
    List<Irrigacao> findByStatus(Irrigacao.Status status);
}
