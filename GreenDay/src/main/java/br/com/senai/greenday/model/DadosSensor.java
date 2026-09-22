package br.com.senai.greenday.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dados_sensor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DadosSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    @JsonIgnore
    private Sensor sensor;

    @Column(nullable = false)
    private Double valor;

    @Builder.Default
    private LocalDateTime medidoEm = LocalDateTime.now();
}
