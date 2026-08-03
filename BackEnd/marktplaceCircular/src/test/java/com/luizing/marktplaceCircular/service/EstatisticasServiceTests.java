package com.luizing.marktplaceCircular.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.luizing.marktplaceCircular.dtos.EstatisticasResponseDto;
import com.luizing.marktplaceCircular.model.anuncio.StatusAnuncio;
import com.luizing.marktplaceCircular.model.estatistica.Estatistica;
import com.luizing.marktplaceCircular.repository.AnuncioRepository;
import com.luizing.marktplaceCircular.repository.EstatisticaRepository;
import com.luizing.marktplaceCircular.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EstatisticasServiceTests {

    @Test
    void deveContarAnunciosPorStatus() {
        AnuncioRepository anuncioRepository = mock(AnuncioRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        EstatisticaRepository estatisticaRepository = mock(EstatisticaRepository.class);
        Estatistica estatistica = new Estatistica();
        estatistica.setTotalAnunciosCriados(12);
        EstatisticasService service = new EstatisticasService(
                anuncioRepository, userRepository, estatisticaRepository);

        when(estatisticaRepository.findById(Estatistica.ID_PRINCIPAL))
                .thenReturn(Optional.of(estatistica));
        when(userRepository.count()).thenReturn(8L);
        when(anuncioRepository.countByStatus(StatusAnuncio.DISPONIVEL)).thenReturn(7L);
        when(anuncioRepository.countByStatus(StatusAnuncio.VENDIDO)).thenReturn(3L);
        when(anuncioRepository.countByStatus(StatusAnuncio.DOADO)).thenReturn(2L);

        EstatisticasResponseDto resposta = service.consultar();

        assertEquals(12L, resposta.itensAnunciados());
        assertEquals(8L, resposta.alunosParticipando());
        assertEquals(7L, resposta.itensDisponiveis());
        assertEquals(3L, resposta.itensVendidos());
        assertEquals(2L, resposta.itensDoados());
    }
}
