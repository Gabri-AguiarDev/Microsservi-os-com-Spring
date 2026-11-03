package com.ecoenergia.repository;

import com.ecoenergia.model.MetaConsumo;
import com.ecoenergia.model.StatusMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetaConsumoRepository extends JpaRepository<MetaConsumo, Long> {
    List<MetaConsumo> findByEmpresaIdOrderByVigenteDesdeDesc(Long empresaId);
    List<MetaConsumo> findByEmpresaIdAndStatus(Long empresaId, StatusMeta status);
    Optional<MetaConsumo> findFirstByEmpresaIdAndStatus(Long empresaId, StatusMeta status);
}
