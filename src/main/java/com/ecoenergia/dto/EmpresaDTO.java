package com.ecoenergia.dto;

import com.ecoenergia.model.Empresa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class EmpresaDTO {
    private EmpresaDTO() {}

    public record Request(
            @NotBlank(message = "Nome é obrigatório") @Size(max = 150) String nome,
            @NotBlank(message = "CNPJ é obrigatório") @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos numéricos") String cnpj,
            @Size(max = 100) String setor,
            @Email(message = "E-mail inválido") @Size(max = 150) String email) {}

    public record Response(Long id, String nome, String cnpj, String setor, String email, LocalDateTime criadoEm) {
        public static Response from(Empresa e) {
            return new Response(e.getId(), e.getNome(), e.getCnpj(), e.getSetor(), e.getEmail(), e.getCriadoEm());
        }
    }
}
