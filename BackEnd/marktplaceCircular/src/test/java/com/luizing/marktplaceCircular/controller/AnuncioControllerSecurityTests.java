package com.luizing.marktplaceCircular.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luizing.marktplaceCircular.dtos.AnuncioDto;
import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.dtos.AtualizarStatusAnuncioDto;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.model.anuncio.StatusAnuncio;
import com.luizing.marktplaceCircular.model.anuncio.TipoAnuncio;
import com.luizing.marktplaceCircular.service.AnuncioService;
import com.luizing.marktplaceCircular.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

class AnuncioControllerSecurityTests {

    @Test
    void deveBloquearCriacaoQuandoTokenPertenceAOutroUsuario() {
        AnuncioService anuncioService = mock(AnuncioService.class);
        UserService userService = mock(UserService.class);
        Authentication authentication = mock(Authentication.class);
        AnuncioController controller = new AnuncioController(anuncioService, userService);
        AnuncioDto anuncio = new AnuncioDto(
                "Livro",
                "Descricao",
                CategoriaAnuncio.LIVROS,
                TipoAnuncio.VENDA,
                10,
                "https://exemplo.com/livro.png",
                2L
        );

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("usuario-1");
        when(userService.usuarioEhAutenticado(2L, "usuario-1")).thenReturn(false);

        assertEquals(HttpStatus.FORBIDDEN, controller.criar(anuncio, authentication).getStatusCode());
        verify(anuncioService, never()).criar(anuncio);
    }

    @Test
    void devePermitirEncerramentoPeloDonoAutenticado() {
        AnuncioService anuncioService = mock(AnuncioService.class);
        UserService userService = mock(UserService.class);
        Authentication authentication = mock(Authentication.class);
        AnuncioController controller = new AnuncioController(anuncioService, userService);
        AnuncioResponseDto anuncioEncerrado = new AnuncioResponseDto(
                1L,
                "Livro",
                "Descricao",
                CategoriaAnuncio.LIVROS,
                TipoAnuncio.VENDA,
                StatusAnuncio.VENDIDO,
                10,
                "https://exemplo.com/livro.png",
                1L,
                0
        );

        when(authentication.getName()).thenReturn("dono");
        when(userService.buscarIdPorLogin("dono")).thenReturn(Optional.of(1L));
        when(anuncioService.usuarioEhDono(1L, 1L)).thenReturn(true);
        when(anuncioService.encerrar(1L, StatusAnuncio.VENDIDO))
                .thenReturn(Optional.of(anuncioEncerrado));

        assertEquals(HttpStatus.OK, controller.encerrar(
                1L,
                new AtualizarStatusAnuncioDto(StatusAnuncio.VENDIDO),
                authentication
        ).getStatusCode());
    }
}
