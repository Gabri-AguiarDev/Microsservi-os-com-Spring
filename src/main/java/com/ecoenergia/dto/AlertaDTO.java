package com.ecoenergia.dto;

import com.ecoenergia.model.Alerta;
import com.ecoenergia.model.StatusAlerta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class AlertaDTO {
    private AlertaDTO() {}

    public record Response(Long id, Long empresaId, String competencia, BigDecimal consumoKwh, BigDecimal limiteKwh,
                           String mensagem, StatusAlerta status, LocalDateTime criadoEm, LocalDateTime resolvidoEm) {
        public static Response from(Alerta a) {
            return new Response(a.getId(), a.getEmpresa().getId(), a.getCompetencia(), a.getConsumoKwh(), a.getLimiteKwh(),
                    a.getMensagem(), a.getStatus(), a.getCriadoEm(), a.getResolvidoEm());
        }
    }
}
