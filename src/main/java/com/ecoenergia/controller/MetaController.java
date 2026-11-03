package com.ecoenergia.controller;

import com.ecoenergia.dto.MetaDTO;
import com.ecoenergia.service.MetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;

    @PostMapping("/empresas/{empresaId}/metas")
    public ResponseEntity<MetaDTO.Response> criar(@PathVariable Long empresaId, @Valid @RequestBody MetaDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(metaService.criar(empresaId, req));
    }

    @GetMapping("/empresas/{empresaId}/metas")
    public List<MetaDTO.Response> listar(@PathVariable Long empresaId) {
        return metaService.listar(empresaId);
    }

    @PutMapping("/metas/{id}")
    public MetaDTO.Response atualizar(@PathVariable Long id, @Valid @RequestBody MetaDTO.Request req) {
        return metaService.atualizar(id, req);
    }

    @DeleteMapping("/metas/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        metaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
