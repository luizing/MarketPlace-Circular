package com.luizing.marktplaceCircular.dtos;

import java.util.List;
import org.springframework.data.domain.Page;

public record PaginaResponseDto<T>(
        List<T> conteudo,
        int pagina,
        int tamanho,
        long totalItens,
        int totalPaginas,
        boolean primeira,
        boolean ultima
) {

    public static <T> PaginaResponseDto<T> fromPage(Page<T> pagina) {
        return new PaginaResponseDto<>(
                pagina.getContent(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages(),
                pagina.isFirst(),
                pagina.isLast()
        );
    }
}
