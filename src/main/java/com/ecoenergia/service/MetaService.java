package com.ecoenergia.service;

import com.ecoenergia.dto.MetaDTO;
import com.ecoenergia.exception.RecursoNaoEncontradoException;
import com.ecoenergia.model.Empresa;
import com.ecoenergia.model.MetaConsumo;
import com.ecoenergia.model.StatusMeta;
import com.ecoenergia.repository.MetaConsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetaService {

    private final MetaConsumoRepository metaRepository;
    private final EmpresaService empresaService;

    @Transactional(readOnly = true)
    public List<MetaDTO.Response> listar(Long empresaId) {
        empresaService.buscarEntidade(empresaId);
        return metaRepository.findByEmpresaIdOrderByVigenteDesdeDesc(empresaId).stream().map(MetaDTO.Response::from).toList();
    }

    /** Ao criar uma nova meta, a meta ativa anterior da empresa é inativada. */
    @Transactional
    public MetaDTO.Response criar(Long empresaId, MetaDTO.Request req) {
        Empresa empresa = empresaService.buscarEntidade(empresaId);
        List<MetaConsumo> ativas = metaRepository.findByEmpresaIdAndStatus(empresaId, StatusMeta.ATIVA);
        ativas.forEach(m -> m.setStatus(StatusMeta.INATIVA));
        metaRepository.saveAll(ativas);

        MetaConsumo nova = MetaConsumo.builder().empresa(empresa).limiteMensalKwh(req.limiteMensalKwh())
                .vigenteDesde(req.vigenteDesde()).status(StatusMeta.ATIVA).build();
        return MetaDTO.Response.from(metaRepository.save(nova));
    }

    @Transactional
    public MetaDTO.Response atualizar(Long id, MetaDTO.Request req) {
        MetaConsumo m = buscar(id);
        m.setLimiteMensalKwh(req.limiteMensalKwh());
        m.setVigenteDesde(req.vigenteDesde());
        return MetaDTO.Response.from(metaRepository.save(m));
    }

    @Transactional
    public void remover(Long id) {
        metaRepository.delete(buscar(id));
    }

    private MetaConsumo buscar(Long id) {
        return metaRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Meta", id));
    }
}
