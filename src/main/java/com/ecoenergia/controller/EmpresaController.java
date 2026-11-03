package com.ecoenergia.controller;

import com.ecoenergia.dto.EmpresaDTO;
import com.ecoenergia.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public Page<EmpresaDTO.Response> listar(@PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return empresaService.listar(pageable);
    }

    @GetMapping("/{id}")
    public EmpresaDTO.Response buscar(@PathVariable Long id) {
        return empresaService.buscar(id);
    }

    @PostMapping
    public ResponseEntity<EmpresaDTO.Response> criar(@Valid @RequestBody EmpresaDTO.Request req) {
        EmpresaDTO.Response criada = empresaService.criar(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @PutMapping("/{id}")
    public EmpresaDTO.Response atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaDTO.Request req) {
        return empresaService.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        empresaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
