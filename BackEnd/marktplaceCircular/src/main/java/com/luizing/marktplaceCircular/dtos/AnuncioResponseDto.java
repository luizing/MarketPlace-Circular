package com.luizing.marktplaceCircular.dtos;

import com.luizing.marktplaceCircular.model.Anuncio;
import com.luizing.marktplaceCircular.model.CategoriaAnuncio;
import com.luizing.marktplaceCircular.model.TipoAnuncio;

public record AnuncioResponseDto(
        Long id,
        String titulo,
        String descricao,
        CategoriaAnuncio categoria,
        TipoAnuncio tipo,
        double preco,
        String imagem
) {

    public static AnuncioResponseDto fromAnuncio(Anuncio anuncio) {
        return new AnuncioResponseDto(
                anuncio.getId(),
                anuncio.getTitulo(),
                anuncio.getDescricao(),
                anuncio.getCategoria(),
                anuncio.getTipo(),
                anuncio.getPreco(),
                anuncio.getImagem()
        );
    }
}
