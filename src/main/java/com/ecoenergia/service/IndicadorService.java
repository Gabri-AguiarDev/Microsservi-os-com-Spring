package com.ecoenergia.service;

import com.ecoenergia.dto.IndicadorDTO;
import com.ecoenergia.exception.RegraNegocioException;
import com.ecoenergia.model.Empresa;
import com.ecoenergia.model.FonteEnergia;
import com.ecoenergia.model.StatusAlerta;
import com.ecoenergia.model.StatusMeta;
import com.ecoenergia.repository.AlertaRepository;
import com.ecoenergia.repository.EmpresaRepository;
import com.ecoenergia.repository.MedicaoRepository;
import com.ecoenergia.repository.MetaConsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Calcula os indicadores de sustentabilidade de uma empresa.
 *
 * Score (0-100) = 50% percentual de energia renovável + 50% aderência à meta de consumo.
 * Classificação: A (>= 80), B (>= 60), C (>= 40), D (< 40).
 */
@Service
@RequiredArgsConstructor
public class IndicadorService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final List<FonteEnergia> RENOVAVEIS =
            Arrays.stream(FonteEnergia.values()).filter(FonteEnergia::isRenovavel).toList();

    private final EmpresaService empresaService;
    private final EmpresaRepository empresaRepository;
    private final MedicaoRepository medicaoRepository;
    private final MetaConsumoRepository metaRepository;
    private final AlertaRepository alertaRepository;

    @Value("${app.indicadores.fator-emissao-kg-co2-por-kwh}")
    private BigDecimal fatorEmissao;

    @Transactional(readOnly = true)
    public IndicadorDTO.Empresa indicadoresDaEmpresa(Long empresaId, LocalDate inicio, LocalDate fim) {
        Empresa empresa = empresaService.buscarEntidade(empresaId);
        LocalDate[] periodo = resolverPeriodo(inicio, fim);
        return calcular(empresa, periodo[0], periodo[1]);
    }

    @Transactional(readOnly = true)
    public IndicadorDTO.Geral indicadoresGerais(LocalDate inicio, LocalDate fim) {
        LocalDate[] periodo = resolverPeriodo(inicio, fim);
        List<IndicadorDTO.Empresa> ranking = empresaRepository.findAll().stream()
                .map(e -> calcular(e, periodo[0], periodo[1]))
                .sorted(Comparator.comparing(IndicadorDTO.Empresa::scoreSustentabilidade).reversed())
                .toList();

        BigDecimal consumo = ranking.stream().map(IndicadorDTO.Empresa::consumoTotalKwh).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal emissao = ranking.stream().map(IndicadorDTO.Empresa::emissaoEstimadaKgCo2).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal scoreMedio = ranking.isEmpty() ? BigDecimal.ZERO
                : ranking.stream().map(IndicadorDTO.Empresa::scoreSustentabilidade).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(ranking.size()), 1, RoundingMode.HALF_UP);

        return new IndicadorDTO.Geral(periodo[0], periodo[1], ranking.size(), consumo, emissao, scoreMedio, ranking);
    }

    private IndicadorDTO.Empresa calcular(Empresa empresa, LocalDate ini, LocalDate fim) {
        Long id = empresa.getId();
        BigDecimal total = nz(medicaoRepository.somaConsumo(id, ini, fim));
        BigDecimal renovavel = nz(medicaoRepository.somaConsumoPorFontes(id, ini, fim, RENOVAVEIS));
        BigDecimal custo = nz(medicaoRepository.somaCusto(id, ini, fim));
        BigDecimal naoRenovavel = total.subtract(renovavel);

        long meses = Math.max(1, ChronoUnit.MONTHS.between(ini.withDayOfMonth(1), fim.withDayOfMonth(1)) + 1);
        BigDecimal percRenovavel = total.signum() == 0 ? BigDecimal.ZERO
                : renovavel.multiply(CEM).divide(total, 2, RoundingMode.HALF_UP);
        BigDecimal media = total.divide(BigDecimal.valueOf(meses), 2, RoundingMode.HALF_UP);
        BigDecimal emissao = naoRenovavel.multiply(fatorEmissao).setScale(2, RoundingMode.HALF_UP);

        BigDecimal metaMensal = metaRepository.findFirstByEmpresaIdAndStatus(id, StatusMeta.ATIVA)
                .map(m -> m.getLimiteMensalKwh()).orElse(null);

        BigDecimal desvio = null;
        BigDecimal aderencia = BigDecimal.valueOf(50); // sem meta definida: neutro
        if (metaMensal != null) {
            BigDecimal metaPeriodo = metaMensal.multiply(BigDecimal.valueOf(meses));
            if (total.signum() == 0) {
                aderencia = CEM;
                desvio = BigDecimal.valueOf(-100).setScale(2);
            } else {
                desvio = total.subtract(metaPeriodo).multiply(CEM).divide(metaPeriodo, 2, RoundingMode.HALF_UP);
                aderencia = metaPeriodo.multiply(CEM).divide(total, 2, RoundingMode.HALF_UP).min(CEM);
            }
        }

        BigDecimal score = percRenovavel.multiply(BigDecimal.valueOf(0.5))
                .add(aderencia.multiply(BigDecimal.valueOf(0.5))).setScale(1, RoundingMode.HALF_UP);
        long abertos = alertaRepository.countByEmpresaIdAndStatus(id, StatusAlerta.ABERTO);

        return new IndicadorDTO.Empresa(id, empresa.getNome(), ini, fim, meses, total, renovavel, naoRenovavel,
                percRenovavel, media, custo, emissao, metaMensal, desvio, abertos, score, classificar(score));
    }

    private String classificar(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(80)) >= 0) return "A - Excelente";
        if (score.compareTo(BigDecimal.valueOf(60)) >= 0) return "B - Bom";
        if (score.compareTo(BigDecimal.valueOf(40)) >= 0) return "C - Regular";
        return "D - Crítico";
    }

    /** Período padrão: últimos 12 meses (a partir do primeiro dia do mês). */
    private LocalDate[] resolverPeriodo(LocalDate inicio, LocalDate fim) {
        LocalDate fi = fim != null ? fim : LocalDate.now();
        LocalDate ini = inicio != null ? inicio : fi.withDayOfMonth(1).minusMonths(11);
        if (ini.isAfter(fi)) {
            throw new RegraNegocioException("A data inicial não pode ser posterior à data final");
        }
        return new LocalDate[]{ini, fi};
    }

    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }
}
