package br.com.senai.greenday.service;

import br.com.senai.greenday.exception.ResourceNotFoundException;
import br.com.senai.greenday.model.Esp32;
import br.com.senai.greenday.repository.Esp32Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Esp32Service {

    private final Esp32Repository esp32Repository;
    private final PropriedadeService propriedadeService;

    public Esp32 registrar(Long propriedadeId, Esp32 dispositivo) {
        dispositivo.setPropriedade(propriedadeService.buscarPorId(propriedadeId));
        dispositivo.setOnline(true);
        dispositivo.setUltimoContato(LocalDateTime.now());
        return esp32Repository.save(dispositivo);
    }

    public List<Esp32> listarPorPropriedade(Long propriedadeId) {
        return esp32Repository.findByPropriedadeId(propriedadeId);
    }

    public Esp32 buscarPorCodigo(String codigo) {
        return esp32Repository.findByCodigoDispositivo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo nao encontrado: " + codigo));
    }

    public void registrarHeartbeat(String codigoDispositivo) {
        Esp32 dispositivo = buscarPorCodigo(codigoDispositivo);
        dispositivo.setOnline(true);
        dispositivo.setUltimoContato(LocalDateTime.now());
        esp32Repository.save(dispositivo);
    }

    public void marcarOfflineInativos(int minutosLimite) {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(minutosLimite);
        esp32Repository.findAll().forEach(d -> {
            if (d.getUltimoContato() != null && d.getUltimoContato().isBefore(limite) && d.isOnline()) {
                d.setOnline(false);
                esp32Repository.save(d);
            }
        });
    }
}
