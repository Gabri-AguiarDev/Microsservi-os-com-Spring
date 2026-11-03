package com.ecoenergia.dto;

import com.ecoenergia.model.Perfil;
import com.ecoenergia.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDTO {
    private AuthDTO() {}

    public record RegistroRequest(
            @NotBlank(message = "Nome é obrigatório") @Size(max = 120) String nome,
            @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email,
            @NotBlank(message = "Senha é obrigatória") @Size(min = 8, max = 60, message = "Senha deve ter entre 8 e 60 caracteres") String senha) {}

    public record LoginRequest(
            @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email,
            @NotBlank(message = "Senha é obrigatória") String senha) {}

    public record TokenResponse(String token, String tipo, long expiraEmMinutos, String perfil) {}

    public record UsuarioResponse(Long id, String nome, String email, Perfil perfil) {
        public static UsuarioResponse from(Usuario u) {
            return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil());
        }
    }
}
