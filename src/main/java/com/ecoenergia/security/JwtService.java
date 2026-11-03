package com.ecoenergia.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMinutos;

    public JwtService(@Value("${app.jwt.secret}") String segredo,
                      @Value("${app.jwt.expiration-minutes}") long expiracaoMinutos) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public String gerarToken(String email, String perfil) {
        Date agora = new Date();
        Date expira = new Date(agora.getTime() + expiracaoMinutos * 60_000);
        return Jwts.builder()
                .subject(email)
                .claim("perfil", perfil)
                .issuedAt(agora)
                .expiration(expira)
                .signWith(chave)
                .compact();
    }

    /** Lança JwtException se o token for inválido ou estiver expirado. */
    public String extrairEmail(String token) {
        return Jwts.parser().verifyWith(chave).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public long getExpiracaoMinutos() { return expiracaoMinutos; }
}
