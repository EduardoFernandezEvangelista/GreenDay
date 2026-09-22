package br.com.senai.greenday.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerta {

    public enum Severidade { BAIXA, MEDIA, ALTA, CRITICA }
    public enum Tipo { SECA, GEADA, PRAGA, UMIDADE_BAIXA, UMIDADE_ALTA, DISPOSITIVO_OFFLINE, OUTRO }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    @JsonIgnore
    private Propriedade propriedade;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Enumerated(EnumType.STRING)
    private Severidade severidade;

    @Column(length = 500)
    private String mensagem;

    @Builder.Default
    private boolean lido = false;

    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();
}
