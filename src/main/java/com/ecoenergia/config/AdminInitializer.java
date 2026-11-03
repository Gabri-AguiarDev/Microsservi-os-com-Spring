package com.ecoenergia.config;

import com.ecoenergia.model.Perfil;
import com.ecoenergia.model.Usuario;
import com.ecoenergia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Cria o usuário administrador na primeira execução (credenciais vêm de variáveis de ambiente). */
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.nome}") private String nome;
    @Value("${app.admin.email}") private String email;
    @Value("${app.admin.senha}") private String senha;

    @Override
    public void run(String... args) {
        if (!usuarioRepository.existsByEmail(email)) {
            usuarioRepository.save(Usuario.builder()
                    .nome(nome).email(email).senha(passwordEncoder.encode(senha)).perfil(Perfil.ADMIN).build());
            log.info("Usuário administrador criado: {}", email);
        }
    }
}
