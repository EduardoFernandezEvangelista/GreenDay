package br.com.senai.greenday.service;

import br.com.senai.greenday.exception.ResourceNotFoundException;
import br.com.senai.greenday.model.DadosSensor;
import br.com.senai.greenday.model.Sensor;
import br.com.senai.greenday.repository.DadosSensorRepository;
import br.com.senai.greenday.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final DadosSensorRepository dadosSensorRepository;
    private final PropriedadeService propriedadeService;

    public Sensor criar(Long propriedadeId, Sensor sensor) {
        sensor.setPropriedade(propriedadeService.buscarPorId(propriedadeId));
        return sensorRepository.save(sensor);
    }

    public List<Sensor> listarPorPropriedade(Long propriedadeId) {
        return sensorRepository.findByPropriedadeId(propriedadeId);
    }

    public Sensor buscarPorId(Long id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor nao encontrado: " + id));
    }

    public Sensor atualizar(Long id, Sensor dados) {
        Sensor sensor = buscarPorId(id);
        sensor.setNome(dados.getNome());
        sensor.setTipo(dados.getTipo());
        sensor.setUnidadeMedida(dados.getUnidadeMedida());
        return sensorRepository.save(sensor);
    }

    public void excluir(Long id) {
        sensorRepository.delete(buscarPorId(id));
    }

    public DadosSensor registrarLeitura(Long sensorId, Double valor) {
        Sensor sensor = buscarPorId(sensorId);
        DadosSensor dados = DadosSensor.builder()
                .sensor(sensor)
                .valor(valor)
                .medidoEm(LocalDateTime.now())
                .build();
        return dadosSensorRepository.save(dados);
    }

    public List<DadosSensor> historico(Long sensorId) {
        return dadosSensorRepository.findBySensorIdOrderByMedidoEmDesc(sensorId);
    }

    public Optional<Double> ultimaLeitura(Long sensorId) {
        return dadosSensorRepository.findBySensorIdOrderByMedidoEmDesc(sensorId)
                .stream()
                .findFirst()
                .map(DadosSensor::getValor);
    }
}
