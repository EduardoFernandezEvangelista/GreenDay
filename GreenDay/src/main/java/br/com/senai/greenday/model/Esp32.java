package br.com.senai.greenday.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "esp32_dispositivos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Esp32 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigoDispositivo;

    private String apelido;
    private String enderecoIp;
    private String firmwareVersao;

    @Builder.Default
    private boolean online = false;

    private LocalDateTime ultimoContato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id")
    @JsonIgnore
    private Propriedade propriedade;
}
