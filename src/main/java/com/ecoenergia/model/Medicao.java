package com.ecoenergia.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "TB_MEDICAO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Medicao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPRESA_ID", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private LocalDate dataMedicao;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal consumoKwh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FonteEnergia fonte;

    @Column(precision = 14, scale = 2)
    private BigDecimal custo;
}
