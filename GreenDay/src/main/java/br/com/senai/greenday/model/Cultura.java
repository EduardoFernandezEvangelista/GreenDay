package br.com.senai.greenday.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "culturas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cultura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String variedade;
    private LocalDate dataPlantio;
    private LocalDate previsaoColheita;
    private Double areaOcupadaHectares;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    @JsonIgnore
    private Propriedade propriedade;
}
