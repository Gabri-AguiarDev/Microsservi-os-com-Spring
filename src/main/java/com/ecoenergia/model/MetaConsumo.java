package com.ecoenergia.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "TB_META_CONSUMO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MetaConsumo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMPRESA_ID", nullable = false)
    private Empresa empresa;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal limiteMensalKwh;

    @Column(nullable = false)
    private LocalDate vigenteDesde;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StatusMeta status;
}
