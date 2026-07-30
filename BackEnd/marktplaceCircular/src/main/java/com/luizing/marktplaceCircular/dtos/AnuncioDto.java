package com.luizing.marktplaceCircular.dtos;

import com.luizing.marktplaceCircular.model.anuncio.Anuncio;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.model.anuncio.TipoAnuncio;

public record AnuncioDto(
        String titulo,
        String descricao,
        CategoriaAnuncio categoria,
        TipoAnuncio tipo,
        double preco,
        String imagem
) {

    public Anuncio toAnuncio() {
        Anuncio anuncio = new Anuncio();
        anuncio.setTitulo(titulo);
        anuncio.setDescricao(descricao);
        anuncio.setCategoria(categoria);
        anuncio.setTipo(tipo);
        anuncio.setPreco(preco);
        anuncio.setImagem(imagem);

        return anuncio;
    }
}
