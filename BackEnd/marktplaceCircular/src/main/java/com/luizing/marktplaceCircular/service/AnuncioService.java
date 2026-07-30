package com.luizing.marktplaceCircular.service;

import com.luizing.marktplaceCircular.dtos.AnuncioDto;
import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.dtos.UserContatoDto;
import com.luizing.marktplaceCircular.model.anuncio.Anuncio;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.repository.AnuncioRepository;
import com.luizing.marktplaceCircular.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final UserRepository userRepository;

    public AnuncioService(AnuncioRepository anuncioRepository, UserRepository userRepository) {
        this.anuncioRepository = anuncioRepository;
        this.userRepository = userRepository;
    }

    public AnuncioResponseDto criar(AnuncioDto dto) {
        Anuncio anuncio = dto.toAnuncio();

        if (dto.usuarioId() != null) {
            userRepository.findById(dto.usuarioId()).ifPresent(anuncio::setUsuario);
        }

        Anuncio anuncioCriado = anuncioRepository.save(anuncio);
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

    @Transactional(readOnly = true)
    public Optional<List<UserContatoDto>> retornarInteressados(Long anuncioId) {
        return anuncioRepository.findById(anuncioId)
                .map(anuncio -> anuncio.getInteressados().stream()
                        .map(usuario -> new UserContatoDto(
                                usuario.getId(), usuario.getLogin(), usuario.getContato()))
                        .toList());
    }

    @Transactional
    public Optional<AnuncioResponseDto> interessar(Long anuncioId, Long usuarioId) {
        return anuncioRepository.findById(anuncioId)
                .flatMap(anuncio -> userRepository.findById(usuarioId)
                        .filter(usuario -> anuncio.getUsuario() == null
                                || !anuncio.getUsuario().getId().equals(usuario.getId()))
                        .map(usuario -> {
                            if (!usuario.getItensInteressados().contains(anuncio)) {
                                usuario.getItensInteressados().add(anuncio);
                                userRepository.save(usuario);
                            }
                            return AnuncioResponseDto.fromAnuncio(anuncio);
                        }));
    }

    @Transactional(readOnly = true)
    public boolean usuarioEhDono(Long anuncioId, Long usuarioId) {
        return anuncioRepository.findById(anuncioId)
                .map(anuncio -> anuncio.getUsuario() != null
                        && anuncio.getUsuario().getId().equals(usuarioId))
                .orElse(false);
    }

    @Transactional
    public Optional<AnuncioResponseDto> desinteressar(Long anuncioId, Long usuarioId) {
        return anuncioRepository.findById(anuncioId)
                .flatMap(anuncio -> userRepository.findById(usuarioId)
                        .map(usuario -> {
                            usuario.getItensInteressados().remove(anuncio);
                            userRepository.save(usuario);
                            return AnuncioResponseDto.fromAnuncio(anuncio);
                        }));
    }

    @Transactional(readOnly = true)
    public Optional<Boolean> verificarInteresse(Long anuncioId, Long usuarioId) {
        return anuncioRepository.findById(anuncioId)
                .flatMap(anuncio -> userRepository.findById(usuarioId)
                        .map(usuario -> usuario.getItensInteressados().contains(anuncio)));
    }


    private boolean contemTitulo(Anuncio anuncio, String titulo) {
        return anuncio.getTitulo() != null
                && anuncio.getTitulo().toLowerCase().contains(titulo.toLowerCase());
    }
}
