package com.luizing.marktplaceCircular.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final Key chave;
    private final long expiracaoMs;

    public JwtService(
            @Value("${jwt.secret}") String segredo,
            @Value("${jwt.expiracao-ms}") long expiracaoMs
    ) {
        this.chave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(segredo));
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(String login) {
        Date agora = new Date();

        return Jwts.builder()
                .subject(login)
                .issuedAt(agora)
                .expiration(new Date(agora.getTime() + expiracaoMs))
                .signWith(chave)
                .compact();
    }

    public String extrairLogin(String token) {
        return extrairClaims(token).getSubject();
    }

    public boolean tokenValido(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
