package com.ecoenergia.controller;

import com.ecoenergia.dto.IndicadorDTO;
import com.ecoenergia.service.IndicadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class IndicadorController {

    private final IndicadorService indicadorService;

    /** Indicadores de sustentabilidade de uma empresa (padrão: últimos 12 meses). */
    @GetMapping("/empresas/{empresaId}/indicadores")
    public IndicadorDTO.Empresa indicadoresDaEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return indicadorService.indicadoresDaEmpresa(empresaId, inicio, fim);
    }

    /** Visão geral e ranking de sustentabilidade de todas as empresas. */
    @GetMapping("/indicadores-sustentabilidade")
    public IndicadorDTO.Geral indicadoresGerais(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return indicadorService.indicadoresGerais(inicio, fim);
    }
}
