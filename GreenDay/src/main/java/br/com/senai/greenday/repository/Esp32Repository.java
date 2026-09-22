package br.com.senai.greenday.repository;

import br.com.senai.greenday.model.Esp32;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Esp32Repository extends JpaRepository<Esp32, Long> {
    Optional<Esp32> findByCodigoDispositivo(String codigoDispositivo);
    List<Esp32> findByPropriedadeId(Long propriedadeId);
}
