package com.luizing.marktplaceCircular.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.luizing.marktplaceCircular.dtos.AnuncioDto;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.model.anuncio.TipoAnuncio;
import org.junit.jupiter.api.Test;

class ValidacaoDadosTests {

    @Test
    void deveAceitarAnuncioValido() {
        AnuncioDto anuncio = new AnuncioDto(
                "Livro de Calculo",
                "Em bom estado.",
                CategoriaAnuncio.LIVROS,
                TipoAnuncio.VENDA,
                30,
                "https://exemplo.com/imagem.png",
                1L
        );

        assertDoesNotThrow(() -> ValidacaoDados.validarAnuncio(anuncio));
    }

    @Test
    void deveRejeitarUrlDeImagemInvalida() {
        AnuncioDto anuncio = new AnuncioDto(
                "Livro de Calculo",
                "Em bom estado.",
                CategoriaAnuncio.LIVROS,
                TipoAnuncio.VENDA,
                30,
                "arquivo-local.png",
                1L
        );

        assertThrows(DadosInvalidosException.class, () -> ValidacaoDados.validarAnuncio(anuncio));
    }

    @Test
    void deveRejeitarTituloComMaisDeVinteECincoCaracteres() {
        AnuncioDto anuncio = new AnuncioDto(
                "Titulo com mais de vinte e cinco caracteres",
                "Em bom estado.",
                CategoriaAnuncio.LIVROS,
                TipoAnuncio.VENDA,
                30,
                "https://exemplo.com/imagem.png",
                1L
        );

        assertThrows(DadosInvalidosException.class, () -> ValidacaoDados.validarAnuncio(anuncio));
    }

    @Test
    void deveRejeitarDescricaoComMaisDeDuzentosCaracteres() {
        AnuncioDto anuncio = new AnuncioDto(
                "Livro de Calculo",
                "a".repeat(201),
                CategoriaAnuncio.LIVROS,
                TipoAnuncio.VENDA,
                30,
                "https://exemplo.com/imagem.png",
                1L
        );

        assertThrows(DadosInvalidosException.class, () -> ValidacaoDados.validarAnuncio(anuncio));
    }
}
