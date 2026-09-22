package br.com.senai.greenday.repository;

import br.com.senai.greenday.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByPropriedadeId(Long propriedadeId);
    List<Sensor> findByEsp32Id(Long esp32Id);
}
