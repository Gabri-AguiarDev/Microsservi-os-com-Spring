package com.ecoenergia.service;

import com.ecoenergia.dto.MedicaoDTO;
import com.ecoenergia.exception.RecursoNaoEncontradoException;
import com.ecoenergia.exception.RegraNegocioException;
import com.ecoenergia.model.*;
import com.ecoenergia.repository.AlertaRepository;
import com.ecoenergia.repository.MedicaoRepository;
import com.ecoenergia.repository.MetaConsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class MedicaoService {

    private final MedicaoRepository medicaoRepository;
    private final MetaConsumoRepository metaRepository;
    private final AlertaRepository alertaRepository;
    private final EmpresaService empresaService;

    @Transactional(readOnly = true)
    public Page<MedicaoDTO.Response> listar(Long empresaId, LocalDate inicio, LocalDate fim, Pageable pageable) {
        empresaService.buscarEntidade(empresaId);
        LocalDate ini = inicio != null ? inicio : LocalDate.of(2000, 1, 1);
        LocalDate fi = fim != null ? fim : LocalDate.now();
        if (ini.isAfter(fi)) {
            throw new RegraNegocioException("A data inicial não pode ser posterior à data final");
        }
        return medicaoRepository.findByEmpresaIdAndDataMedicaoBetween(empresaId, ini, fi, pageable).map(MedicaoDTO.Response::from);
    }

    @Transactional(readOnly = true)
    public MedicaoDTO.Response buscar(Long id) {
        return MedicaoDTO.Response.from(buscarEntidade(id));
    }

    @Transactional
    public MedicaoDTO.Response criar(Long empresaId, MedicaoDTO.Request req) {
        Empresa empresa = empresaService.buscarEntidade(empresaId);
        Medicao m = Medicao.builder().empresa(empresa).dataMedicao(req.dataMedicao())
                .consumoKwh(req.consumoKwh()).fonte(req.fonte()).custo(req.custo()).build();
        m = medicaoRepository.save(m);
        verificarLimite(empresa, req.dataMedicao());
        return MedicaoDTO.Response.from(m);
    }

    @Transactional
    public MedicaoDTO.Response atualizar(Long id, MedicaoDTO.Request req) {
        Medicao m = buscarEntidade(id);
        m.setDataMedicao(req.dataMedicao());
        m.setConsumoKwh(req.consumoKwh());
        m.setFonte(req.fonte());
        m.setCusto(req.custo());
        m = medicaoRepository.saveAndFlush(m);
        verificarLimite(m.getEmpresa(), req.dataMedicao());
        return MedicaoDTO.Response.from(m);
    }

    @Transactional
    public void remover(Long id) {
        medicaoRepository.delete(buscarEntidade(id));
    }

    private Medicao buscarEntidade(Long id) {
        return medicaoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Medição", id));
    }

    /** Gera alerta automático quando o consumo do mês ultrapassa o limite da meta ativa. */
    private void verificarLimite(Empresa empresa, LocalDate data) {
        metaRepository.findFirstByEmpresaIdAndStatus(empresa.getId(), StatusMeta.ATIVA).ifPresent(meta -> {
            YearMonth ym = YearMonth.from(data);
            BigDecimal total = medicaoRepository.somaConsumo(empresa.getId(), ym.atDay(1), ym.atEndOfMonth());
            if (total == null || total.compareTo(meta.getLimiteMensalKwh()) <= 0) {
                return;
            }
            String competencia = ym.toString();
            if (alertaRepository.existsByEmpresaIdAndCompetenciaAndStatus(empresa.getId(), competencia, StatusAlerta.ABERTO)) {
                return;
            }
            String msg = "Consumo de %s kWh em %s excedeu a meta mensal de %s kWh."
                    .formatted(total.toPlainString(), competencia, meta.getLimiteMensalKwh().toPlainString());
            alertaRepository.save(Alerta.builder().empresa(empresa).competencia(competencia).consumoKwh(total)
                    .limiteKwh(meta.getLimiteMensalKwh()).mensagem(msg).status(StatusAlerta.ABERTO).build());
        });
    }
}
