package com.ecoenergia.dto;

import com.ecoenergia.model.MetaConsumo;
import com.ecoenergia.model.StatusMeta;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class MetaDTO {
    private MetaDTO() {}

    public record Request(
            @NotNull(message = "Limite mensal é obrigatório") @Positive(message = "Limite deve ser maior que zero") @Digits(integer = 12, fraction = 2) BigDecimal limiteMensalKwh,
            @NotNull(message = "Data de vigência é obrigatória") LocalDate vigenteDesde) {}

    public record Response(Long id, Long empresaId, BigDecimal limiteMensalKwh, LocalDate vigenteDesde, StatusMeta status) {
        public static Response from(MetaConsumo m) {
            return new Response(m.getId(), m.getEmpresa().getId(), m.getLimiteMensalKwh(), m.getVigenteDesde(), m.getStatus());
        }
    }
}
