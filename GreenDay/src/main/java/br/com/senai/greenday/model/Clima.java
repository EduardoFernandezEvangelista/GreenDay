package br.com.senai.greenday.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "clima")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    @JsonIgnore
    private Propriedade propriedade;

    private Double temperaturaCelsius;
    private Double umidadeRelativa;
    private Double precipitacaoMm;
    private Double velocidadeVentoKmh;
    private String descricao;

    @Builder.Default
    private LocalDateTime coletadoEm = LocalDateTime.now();
}
