package com.ecoenergia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class IndicadorDTO {
    private IndicadorDTO() {}

    public record Empresa(
            Long empresaId, String empresaNome, LocalDate periodoInicio, LocalDate periodoFim, long meses,
            BigDecimal consumoTotalKwh, BigDecimal consumoRenovavelKwh, BigDecimal consumoNaoRenovavelKwh,
            BigDecimal percentualRenovavel, BigDecimal mediaMensalKwh, BigDecimal custoTotal,
            BigDecimal emissaoEstimadaKgCo2, BigDecimal metaMensalKwh, BigDecimal desvioMetaPercentual,
            long alertasAbertos, BigDecimal scoreSustentabilidade, String classificacao) {}

    public record Geral(
            LocalDate periodoInicio, LocalDate periodoFim, long totalEmpresas,
            BigDecimal consumoTotalKwh, BigDecimal emissaoTotalKgCo2, BigDecimal scoreMedio,
            List<Empresa> ranking) {}
}
