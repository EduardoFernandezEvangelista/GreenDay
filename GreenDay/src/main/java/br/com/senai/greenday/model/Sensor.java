package br.com.senai.greenday.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sensores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {

    public enum Tipo { UMIDADE_SOLO, TEMPERATURA, LUMINOSIDADE, PH_SOLO, CHUVA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;

    private String unidadeMedida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    @JsonIgnore
    private Propriedade propriedade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "esp32_id")
    @JsonIgnore
    private Esp32 esp32;
}
