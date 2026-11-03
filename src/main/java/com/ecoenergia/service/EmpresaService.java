package com.ecoenergia.service;

import com.ecoenergia.dto.EmpresaDTO;
import com.ecoenergia.exception.ConflitoException;
import com.ecoenergia.exception.RecursoNaoEncontradoException;
import com.ecoenergia.model.Empresa;
import com.ecoenergia.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public Page<EmpresaDTO.Response> listar(Pageable pageable) {
        return empresaRepository.findAll(pageable).map(EmpresaDTO.Response::from);
    }

    @Transactional(readOnly = true)
    public EmpresaDTO.Response buscar(Long id) {
        return EmpresaDTO.Response.from(buscarEntidade(id));
    }

    @Transactional
    public EmpresaDTO.Response criar(EmpresaDTO.Request req) {
        if (empresaRepository.existsByCnpj(req.cnpj())) {
            throw new ConflitoException("Já existe uma empresa cadastrada com este CNPJ");
        }
        Empresa e = Empresa.builder().nome(req.nome()).cnpj(req.cnpj()).setor(req.setor()).email(req.email()).build();
        return EmpresaDTO.Response.from(empresaRepository.save(e));
    }

    @Transactional
    public EmpresaDTO.Response atualizar(Long id, EmpresaDTO.Request req) {
        Empresa e = buscarEntidade(id);
        if (empresaRepository.existsByCnpjAndIdNot(req.cnpj(), id)) {
            throw new ConflitoException("Já existe outra empresa cadastrada com este CNPJ");
        }
        e.setNome(req.nome());
        e.setCnpj(req.cnpj());
        e.setSetor(req.setor());
        e.setEmail(req.email());
        return EmpresaDTO.Response.from(empresaRepository.save(e));
    }

    @Transactional
    public void remover(Long id) {
        empresaRepository.delete(buscarEntidade(id));
    }

    public Empresa buscarEntidade(Long id) {
        return empresaRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Empresa", id));
    }
}
