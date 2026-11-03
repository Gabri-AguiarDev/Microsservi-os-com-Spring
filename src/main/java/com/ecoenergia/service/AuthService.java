package com.ecoenergia.service;

import com.ecoenergia.dto.AuthDTO;
import com.ecoenergia.exception.ConflitoException;
import com.ecoenergia.model.Perfil;
import com.ecoenergia.model.Usuario;
import com.ecoenergia.repository.UsuarioRepository;
import com.ecoenergia.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthDTO.UsuarioResponse registrar(AuthDTO.RegistroRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflitoException("Já existe um usuário cadastrado com este e-mail");
        }
        Usuario u = usuarioRepository.save(Usuario.builder()
                .nome(req.nome()).email(email).senha(passwordEncoder.encode(req.senha())).perfil(Perfil.USER).build());
        return AuthDTO.UsuarioResponse.from(u);
    }

    public AuthDTO.TokenResponse login(AuthDTO.LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.senha()));
        Usuario u = usuarioRepository.findByEmail(email).orElseThrow();
        String token = jwtService.gerarToken(u.getEmail(), u.getPerfil().name());
        return new AuthDTO.TokenResponse(token, "Bearer", jwtService.getExpiracaoMinutos(), u.getPerfil().name());
    }
}
