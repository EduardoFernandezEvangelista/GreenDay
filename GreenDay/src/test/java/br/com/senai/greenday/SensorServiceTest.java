package br.com.senai.greenday;

import com.greenday.model.DadosSensor;
import com.greenday.model.Propriedade;
import com.greenday.model.Sensor;
import com.greenday.repository.DadosSensorRepository;
import com.greenday.repository.SensorRepository;
import com.greenday.service.PropriedadeService;
import com.greenday.service.SensorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private DadosSensorRepository dadosSensorRepository;

    @Mock
    private PropriedadeService propriedadeService;

    @InjectMocks
    private SensorService sensorService;

    @Test
    void deveCriarSensorVinculadoAPropriedade() {
        Propriedade propriedade = Propriedade.builder().id(10L).build();
        Sensor sensor = Sensor.builder().nome("Umidade solo 1").tipo(Sensor.Tipo.UMIDADE_SOLO).build();

        when(propriedadeService.buscarPorId(10L)).thenReturn(propriedade);
        when(sensorRepository.save(any(Sensor.class))).thenAnswer(inv -> inv.getArgument(0));

        Sensor resultado = sensorService.criar(10L, sensor);

        assertEquals(propriedade, resultado.getPropriedade());
        verify(sensorRepository).save(sensor);
    }

    @Test
    void deveRegistrarLeituraDeSensor() {
        Sensor sensor = Sensor.builder().id(5L).build();
        when(sensorRepository.findById(5L)).thenReturn(Optional.of(sensor));
        when(dadosSensorRepository.save(any(DadosSensor.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosSensor resultado = sensorService.registrarLeitura(5L, 42.5);

        assertEquals(42.5, resultado.getValor());
        assertEquals(sensor, resultado.getSensor());
    }

    @Test
    void deveRetornarUltimaLeituraDoSensor() {
        DadosSensor leitura = DadosSensor.builder().valor(33.0).build();
        when(dadosSensorRepository.findBySensorIdOrderByMedidoEmDesc(5L)).thenReturn(List.of(leitura));

        Optional<Double> resultado = sensorService.ultimaLeitura(5L);

        assertTrue(resultado.isPresent());
        assertEquals(33.0, resultado.get());
    }
}
