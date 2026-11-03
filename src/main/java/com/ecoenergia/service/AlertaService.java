package com.ecoenergia.service;

import com.ecoenergia.dto.AlertaDTO;
import com.ecoenergia.exception.RecursoNaoEncontradoException;
import com.ecoenergia.exception.RegraNegocioException;
import com.ecoenergia.model.Alerta;
import com.ecoenergia.model.StatusAlerta;
import com.ecoenergia.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;

    @Transactional(readOnly = true)
    public Page<AlertaDTO.Response> listar(Long empresaId, StatusAlerta status, Pageable pageable) {
        Page<Alerta> pagina;
        if (empresaId != null && status != null) {
            pagina = alertaRepository.findByEmpresaIdAndStatus(empresaId, status, pageable);
        } else if (empresaId != null) {
            pagina = alertaRepository.findByEmpresaId(empresaId, pageable);
        } else if (status != null) {
            pagina = alertaRepository.findByStatus(status, pageable);
        } else {
            pagina = alertaRepository.findAll(pageable);
        }
        return pagina.map(AlertaDTO.Response::from);
    }

    @Transactional
    public AlertaDTO.Response resolver(Long id) {
        Alerta a = alertaRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Alerta", id));
        if (a.getStatus() == StatusAlerta.RESOLVIDO) {
            throw new RegraNegocioException("O alerta já foi resolvido");
        }
        a.setStatus(StatusAlerta.RESOLVIDO);
        a.setResolvidoEm(LocalDateTime.now());
        return AlertaDTO.Response.from(alertaRepository.save(a));
    }
}
