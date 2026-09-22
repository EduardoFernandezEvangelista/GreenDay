package br.com.senai.greenday.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "irrigacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Irrigacao {

    public enum Status { AGENDADA, EM_ANDAMENTO, CONCLUIDA, CANCELADA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    @JsonIgnore
    private Propriedade propriedade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cultura_id")
    @JsonIgnore
    private Cultura cultura;

    private LocalDateTime inicio;
    private LocalDateTime fim;
    private Double volumeLitros;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.AGENDADA;

    @Builder.Default
    private boolean automatica = false;
}
