package br.com.senai.greenday.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recomendacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recomendacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    @JsonIgnore
    private Propriedade propriedade;

    @Column(length = 1000)
    private String texto;

    private String categoria;

    @Builder.Default
    private LocalDateTime geradaEm = LocalDateTime.now();
}
