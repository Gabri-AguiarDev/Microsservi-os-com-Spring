package com.ecoenergia.controller;

import com.ecoenergia.dto.AlertaDTO;
import com.ecoenergia.model.StatusAlerta;
import com.ecoenergia.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    public Page<AlertaDTO.Response> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) StatusAlerta status,
            @PageableDefault(size = 20, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        return alertaService.listar(empresaId, status, pageable);
    }

    @PatchMapping("/{id}/resolver")
    public AlertaDTO.Response resolver(@PathVariable Long id) {
        return alertaService.resolver(id);
    }
}
