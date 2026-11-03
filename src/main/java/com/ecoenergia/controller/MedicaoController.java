package com.ecoenergia.controller;

import com.ecoenergia.dto.MedicaoDTO;
import com.ecoenergia.service.MedicaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class MedicaoController {

    private final MedicaoService medicaoService;

    @PostMapping("/empresas/{empresaId}/medicoes")
    public ResponseEntity<MedicaoDTO.Response> criar(@PathVariable Long empresaId, @Valid @RequestBody MedicaoDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicaoService.criar(empresaId, req));
    }

    @GetMapping("/empresas/{empresaId}/medicoes")
    public Page<MedicaoDTO.Response> listar(
            @PathVariable Long empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @PageableDefault(size = 20, sort = "dataMedicao", direction = Sort.Direction.DESC) Pageable pageable) {
        return medicaoService.listar(empresaId, inicio, fim, pageable);
    }

    @GetMapping("/medicoes/{id}")
    public MedicaoDTO.Response buscar(@PathVariable Long id) {
        return medicaoService.buscar(id);
    }

    @PutMapping("/medicoes/{id}")
    public MedicaoDTO.Response atualizar(@PathVariable Long id, @Valid @RequestBody MedicaoDTO.Request req) {
        return medicaoService.atualizar(id, req);
    }

    @DeleteMapping("/medicoes/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        medicaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
