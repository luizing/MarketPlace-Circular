package com.luizing.marktplaceCircular.service;

import com.luizing.marktplaceCircular.dtos.AnuncioDto;
import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.model.Anuncio;
import com.luizing.marktplaceCircular.model.CategoriaAnuncio;
import com.luizing.marktplaceCircular.repository.AnuncioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;

    public AnuncioService(AnuncioRepository anuncioRepository) {
        this.anuncioRepository = anuncioRepository;
    }

    public AnuncioResponseDto criar(AnuncioDto dto) {
        Anuncio anuncioCriado = anuncioRepository.save(dto.toAnuncio());
        return AnuncioResponseDto.fromAnuncio(anuncioCriado);
    }

    public List<AnuncioResponseDto> listar(String titulo, CategoriaAnuncio categoria) {
        return anuncioRepository.findAll().stream()
                .filter(anuncio -> titulo == null || contemTitulo(anuncio, titulo))
                .filter(anuncio -> categoria == null || anuncio.getCategoria() == categoria)
                .map(AnuncioResponseDto::fromAnuncio)
                .toList();
    }

    public Optional<AnuncioResponseDto> buscarPorId(Long id) {
        return anuncioRepository.findById(id)
                .map(AnuncioResponseDto::fromAnuncio);
    }

    public boolean deletar(Long id) {
        if (!anuncioRepository.existsById(id)) {
            return false;
        }

        anuncioRepository.deleteById(id);
        return true;
    }

    private boolean contemTitulo(Anuncio anuncio, String titulo) {
        return anuncio.getTitulo() != null
                && anuncio.getTitulo().toLowerCase().contains(titulo.toLowerCase());
    }
}
