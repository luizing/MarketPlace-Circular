package com.luizing.marktplaceCircular.dtos;

public record EstatisticasResponseDto(
        long itensAnunciados,
        long alunosParticipando,
        long itensDisponiveis,
        long itensVendidos,
        long itensDoados
) {
}
