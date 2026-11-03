package com.ecoenergia.repository;

import com.ecoenergia.model.Alerta;
import com.ecoenergia.model.StatusAlerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    Page<Alerta> findByEmpresaId(Long empresaId, Pageable pageable);
    Page<Alerta> findByStatus(StatusAlerta status, Pageable pageable);
    Page<Alerta> findByEmpresaIdAndStatus(Long empresaId, StatusAlerta status, Pageable pageable);
    boolean existsByEmpresaIdAndCompetenciaAndStatus(Long empresaId, String competencia, StatusAlerta status);
    long countByEmpresaIdAndStatus(Long empresaId, StatusAlerta status);
}
