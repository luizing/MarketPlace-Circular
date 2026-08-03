package com.luizing.marktplaceCircular.service;

import com.luizing.marktplaceCircular.dtos.EstatisticasResponseDto;
import com.luizing.marktplaceCircular.model.estatistica.Estatistica;
import com.luizing.marktplaceCircular.model.anuncio.StatusAnuncio;
import com.luizing.marktplaceCircular.repository.AnuncioRepository;
import com.luizing.marktplaceCircular.repository.EstatisticaRepository;
import com.luizing.marktplaceCircular.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class EstatisticasService {

    private final AnuncioRepository anuncioRepository;
    private final UserRepository userRepository;
    private final EstatisticaRepository estatisticaRepository;

    public EstatisticasService(
            AnuncioRepository anuncioRepository,
            UserRepository userRepository,
            EstatisticaRepository estatisticaRepository
    ) {
        this.anuncioRepository = anuncioRepository;
        this.userRepository = userRepository;
        this.estatisticaRepository = estatisticaRepository;
    }

    @Transactional
    public EstatisticasResponseDto consultar() {
        long itensAnunciados = estatisticaRepository.findById(Estatistica.ID_PRINCIPAL)
                .orElseGet(this::inicializarEstatistica)
                .getTotalAnunciosCriados();
        long alunosParticipando = userRepository.count();

        return new EstatisticasResponseDto(
                itensAnunciados,
                alunosParticipando,
                anuncioRepository.countByStatus(StatusAnuncio.DISPONIVEL),
                anuncioRepository.countByStatus(StatusAnuncio.VENDIDO),
                anuncioRepository.countByStatus(StatusAnuncio.DOADO)
        );
    }

    private Estatistica inicializarEstatistica() {
        Estatistica estatistica = new Estatistica();
        estatistica.setTotalAnunciosCriados(anuncioRepository.count());
        return estatisticaRepository.save(estatistica);
    }
}
