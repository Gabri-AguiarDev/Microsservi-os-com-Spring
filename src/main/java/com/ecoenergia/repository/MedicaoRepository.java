package com.ecoenergia.repository;

import com.ecoenergia.model.FonteEnergia;
import com.ecoenergia.model.Medicao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    Page<Medicao> findByEmpresaIdAndDataMedicaoBetween(Long empresaId, LocalDate inicio, LocalDate fim, Pageable pageable);

    @Query("select sum(m.consumoKwh) from Medicao m where m.empresa.id = :empresaId and m.dataMedicao between :ini and :fim")
    BigDecimal somaConsumo(@Param("empresaId") Long empresaId, @Param("ini") LocalDate ini, @Param("fim") LocalDate fim);

    @Query("select sum(m.consumoKwh) from Medicao m where m.empresa.id = :empresaId and m.dataMedicao between :ini and :fim and m.fonte in :fontes")
    BigDecimal somaConsumoPorFontes(@Param("empresaId") Long empresaId, @Param("ini") LocalDate ini, @Param("fim") LocalDate fim,
                                    @Param("fontes") List<FonteEnergia> fontes);

    @Query("select sum(m.custo) from Medicao m where m.empresa.id = :empresaId and m.dataMedicao between :ini and :fim")
    BigDecimal somaCusto(@Param("empresaId") Long empresaId, @Param("ini") LocalDate ini, @Param("fim") LocalDate fim);
}
