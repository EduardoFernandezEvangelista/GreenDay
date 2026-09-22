package br.com.senai.greenday.repository;

import br.com.senai.greenday.model.DadosSensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DadosSensorRepository extends JpaRepository<DadosSensor, Long> {
    List<DadosSensor> findBySensorIdOrderByMedidoEmDesc(Long sensorId);
    List<DadosSensor> findBySensorIdAndMedidoEmBetween(Long sensorId, LocalDateTime inicio, LocalDateTime fim);
}
