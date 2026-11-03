package com.ecoenergia.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ALERTA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alerta {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPRESA_ID", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, length = 7)
    private String competencia;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal consumoKwh;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal limiteKwh;

    @Column(nullable = false, length = 300)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatusAlerta status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime resolvidoEm;

    @PrePersist
    void antesDeSalvar() { this.criadoEm = LocalDateTime.now(); }
}
