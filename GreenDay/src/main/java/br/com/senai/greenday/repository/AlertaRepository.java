package br.com.senai.greenday.repository;

import br.com.senai.greenday.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByPropriedadeIdOrderByCriadoEmDesc(Long propriedadeId);
    List<Alerta> findByPropriedadeIdAndLidoFalse(Long propriedadeId);
}
