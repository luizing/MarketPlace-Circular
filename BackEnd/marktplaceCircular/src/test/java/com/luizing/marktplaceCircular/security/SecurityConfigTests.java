package com.luizing.marktplaceCircular.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;

class SecurityConfigTests {

    @Test
    void devePermitirOrigemDaVercelNoCors() {
        SecurityConfig securityConfig = new SecurityConfig();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/anuncios");

        CorsConfiguration configuracao = securityConfig.corsConfigurationSource()
                .getCorsConfiguration(request);

        assertTrue(configuracao.getAllowedOrigins()
                .contains("https://market-place-circular.vercel.app"));
        assertTrue(configuracao.getAllowedHeaders().containsAll(List.of("Authorization", "Content-Type")));
    }
}
