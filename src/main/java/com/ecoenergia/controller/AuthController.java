package com.ecoenergia.controller;

import com.ecoenergia.dto.AuthDTO;
import com.ecoenergia.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<AuthDTO.UsuarioResponse> registrar(@Valid @RequestBody AuthDTO.RegistroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(req));
    }

    @PostMapping("/login")
    public AuthDTO.TokenResponse login(@Valid @RequestBody AuthDTO.LoginRequest req) {
        return authService.login(req);
    }
}
