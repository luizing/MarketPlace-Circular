package com.luizing.marktplaceCircular.dtos;

import com.luizing.marktplaceCircular.model.anuncio.Anuncio;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.model.anuncio.TipoAnuncio;

public record AnuncioResponseDto(
        Long id,
        String titulo,
        String descricao,
        CategoriaAnuncio categoria,
        TipoAnuncio tipo,
        double preco,
        String imagem,
        int interessados
) {

    public static AnuncioResponseDto fromAnuncio(Anuncio anuncio) {
        return new AnuncioResponseDto(
                anuncio.getId(),
                anuncio.getTitulo(),
                anuncio.getDescricao(),
                anuncio.getCategoria(),
                anuncio.getTipo(),
                anuncio.getPreco(),
                anuncio.getImagem(),
                anuncio.getInteressados().size()
        );
    }
}
