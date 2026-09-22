package br.com.senai.greenday.repository;

import br.com.senai.greenday.model.Cultura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CulturaRepository extends JpaRepository<Cultura, Long> {
    List<Cultura> findByPropriedadeId(Long propriedadeId);
}
