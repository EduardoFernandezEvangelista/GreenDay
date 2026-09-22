package br.com.senai.greenday.dto;

import br.com.senai.greenday.model.Clima;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClimaDTO {
    private Long id;
    private Double temperaturaCelsius;
    private Double umidadeRelativa;
    private Double precipitacaoMm;
    private Double velocidadeVentoKmh;
    private String descricao;
    private LocalDateTime coletadoEm;

    public static ClimaDTO fromEntity(Clima c) {
        return ClimaDTO.builder()
                .id(c.getId())
                .temperaturaCelsius(c.getTemperaturaCelsius())
                .umidadeRelativa(c.getUmidadeRelativa())
                .precipitacaoMm(c.getPrecipitacaoMm())
                .velocidadeVentoKmh(c.getVelocidadeVentoKmh())
                .descricao(c.getDescricao())
                .coletadoEm(c.getColetadoEm())
                .build();
    }
}
