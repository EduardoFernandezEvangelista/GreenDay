package br.com.senai.greenday.dto;

import br.com.senai.greenday.model.Sensor;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorDTO {
    private Long id;
    private String nome;
    private Sensor.Tipo tipo;
    private String unidadeMedida;
    private Long propriedadeId;
    private Long esp32Id;
    private Double ultimaLeitura;

    public static SensorDTO fromEntity(Sensor s) {
        return SensorDTO.builder()
                .id(s.getId())
                .nome(s.getNome())
                .tipo(s.getTipo())
                .unidadeMedida(s.getUnidadeMedida())
                .propriedadeId(s.getPropriedade() != null ? s.getPropriedade().getId() : null)
                .esp32Id(s.getEsp32() != null ? s.getEsp32().getId() : null)
                .build();
    }
}
