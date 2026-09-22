package br.com.senai.greenday.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "propriedades")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Propriedade {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String endereco;
    private String cidade;
    private String estado;
    private String descricao;
    private Double areaHectares;
    private Double latitude;
    private Double longitude;
    private Double altitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario proprietario;

    @Builder.Default
    @OneToMany(mappedBy = "propriedade", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Cultura> culturas = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "propriedade", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Sensor> sensores = new ArrayList<>();
}
