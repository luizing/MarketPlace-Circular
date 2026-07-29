package com.luizing.marktplaceCircular.dtos;

import com.luizing.marktplaceCircular.model.Anuncio;
import com.luizing.marktplaceCircular.model.CategoriaAnuncio;
import com.luizing.marktplaceCircular.model.TipoAnuncio;

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
