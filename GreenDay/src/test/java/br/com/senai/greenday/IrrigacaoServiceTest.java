package br.com.senai.greenday;

import com.greenday.model.Irrigacao;
import com.greenday.model.Propriedade;
import com.greenday.repository.IrrigacaoRepository;
import com.greenday.service.IrrigacaoService;
import com.greenday.service.PropriedadeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IrrigacaoServiceTest {

    @Mock
    private IrrigacaoRepository irrigacaoRepository;

    @Mock
    private PropriedadeService propriedadeService;

    @InjectMocks
    private IrrigacaoService irrigacaoService;

    @Test
    void deveAgendarIrrigacaoComStatusAgendada() {
        Propriedade propriedade = Propriedade.builder().id(1L).build();
        Irrigacao irrigacao = new Irrigacao();

        when(propriedadeService.buscarPorId(1L)).thenReturn(propriedade);
        when(irrigacaoRepository.save(any(Irrigacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Irrigacao resultado = irrigacaoService.agendar(1L, irrigacao);

        assertEquals(Irrigacao.Status.AGENDADA, resultado.getStatus());
        assertEquals(propriedade, resultado.getPropriedade());
    }

    @Test
    void deveConcluirIrrigacaoComVolumeInformado() {
        Irrigacao irrigacao = Irrigacao.builder().id(3L).status(Irrigacao.Status.EM_ANDAMENTO).build();
        when(irrigacaoRepository.findById(3L)).thenReturn(Optional.of(irrigacao));
        when(irrigacaoRepository.save(any(Irrigacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Irrigacao resultado = irrigacaoService.concluir(3L, 150.0);

        assertEquals(Irrigacao.Status.CONCLUIDA, resultado.getStatus());
        assertEquals(150.0, resultado.getVolumeLitros());
        assertNotNull(resultado.getFim());
    }

    @Test
    void deveCancelarIrrigacao() {
        Irrigacao irrigacao = Irrigacao.builder().id(4L).status(Irrigacao.Status.AGENDADA).build();
        when(irrigacaoRepository.findById(4L)).thenReturn(Optional.of(irrigacao));
        when(irrigacaoRepository.save(any(Irrigacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Irrigacao resultado = irrigacaoService.cancelar(4L);

        assertEquals(Irrigacao.Status.CANCELADA, resultado.getStatus());
    }
}
