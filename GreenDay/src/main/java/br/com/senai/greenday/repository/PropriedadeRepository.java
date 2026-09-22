package br.com.senai.greenday.repository;

import br.com.senai.greenday.model.Propriedade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {
    List<Propriedade> findByProprietarioId(Long usuarioId);
}
