package br.com.senai.greenday.repository;

import br.com.senai.greenday.model.Clima;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClimaRepository extends JpaRepository<Clima, Long> {
    List<Clima> findByPropriedadeIdOrderByColetadoEmDesc(Long propriedadeId);
    Optional<Clima> findFirstByPropriedadeIdOrderByColetadoEmDesc(Long propriedadeId);
}
