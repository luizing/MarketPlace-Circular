package com.luizing.marktplaceCircular.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JwtServiceTests {

    @Test
    void deveGerarTokenValidoComLoginDoUsuario() {
        JwtService jwtService = new JwtService(
                "Y29kaWdvLWRlLXRlc3RlLWNvbS0zMi1ieXRlcy1wYXJhLWp3dA==",
                3_600_000
        );

        String token = jwtService.gerarToken("aluno123");

        assertTrue(jwtService.tokenValido(token));
        assertEquals("aluno123", jwtService.extrairLogin(token));
    }
}
