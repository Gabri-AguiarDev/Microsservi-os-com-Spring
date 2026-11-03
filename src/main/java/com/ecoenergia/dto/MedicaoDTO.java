package com.ecoenergia.dto;

import com.ecoenergia.model.FonteEnergia;
import com.ecoenergia.model.Medicao;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class MedicaoDTO {
    private MedicaoDTO() {}

    public record Request(
            @NotNull(message = "Data da medição é obrigatória") @PastOrPresent(message = "Data da medição não pode ser futura") LocalDate dataMedicao,
            @NotNull(message = "Consumo é obrigatório") @Positive(message = "Consumo deve ser maior que zero") @Digits(integer = 12, fraction = 2) BigDecimal consumoKwh,
            @NotNull(message = "Fonte de energia é obrigatória") FonteEnergia fonte,
            @PositiveOrZero(message = "Custo não pode ser negativo") @Digits(integer = 12, fraction = 2) BigDecimal custo) {}

    public record Response(Long id, Long empresaId, LocalDate dataMedicao, BigDecimal consumoKwh,
                           FonteEnergia fonte, boolean renovavel, BigDecimal custo) {
        public static Response from(Medicao m) {
            return new Response(m.getId(), m.getEmpresa().getId(), m.getDataMedicao(), m.getConsumoKwh(),
                    m.getFonte(), m.getFonte().isRenovavel(), m.getCusto());
        }
    }
}
