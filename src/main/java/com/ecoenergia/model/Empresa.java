package com.ecoenergia.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_EMPRESA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Empresa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(length = 100)
    private String setor;

    @Column(length = 150)
    private String email;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void antesDeSalvar() { this.criadoEm = LocalDateTime.now(); }
}
