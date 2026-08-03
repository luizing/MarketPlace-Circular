package com.luizing.marktplaceCircular.service;

import com.luizing.marktplaceCircular.dtos.AnuncioDto;
import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.dtos.PaginaResponseDto;
import com.luizing.marktplaceCircular.dtos.UserContatoDto;
import com.luizing.marktplaceCircular.model.anuncio.Anuncio;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.model.anuncio.StatusAnuncio;
import com.luizing.marktplaceCircular.model.anuncio.TipoAnuncio;
import com.luizing.marktplaceCircular.model.estatistica.Estatistica;
import com.luizing.marktplaceCircular.repository.AnuncioRepository;
import com.luizing.marktplaceCircular.repository.EstatisticaRepository;
import com.luizing.marktplaceCircular.repository.UserRepository;
import com.luizing.marktplaceCircular.validation.ValidacaoDados;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AnuncioService {

    public static final int LIMITE_ANUNCIOS_POR_USUARIO = 3;

    private final AnuncioRepository anuncioRepository;
    private final UserRepository userRepository;
    private final EstatisticaRepository estatisticaRepository;

    public AnuncioService(
            AnuncioRepository anuncioRepository,
            UserRepository userRepository,
            EstatisticaRepository estatisticaRepository
    ) {
        this.anuncioRepository = anuncioRepository;
        this.userRepository = userRepository;
        this.estatisticaRepository = estatisticaRepository;
    }

    @Transactional
    public Optional<AnuncioResponseDto> criar(AnuncioDto dto) {
        ValidacaoDados.validarAnuncio(dto);

        if (dto.usuarioId() == null || atingiuLimiteAnuncios(dto.usuarioId())) {
            return Optional.empty();
        }

        Anuncio anuncio = dto.toAnuncio();

        return userRepository.findById(dto.usuarioId())
                .map(usuario -> {
                    anuncio.setUsuario(usuario);
                    Anuncio anuncioCriado = anuncioRepository.save(anuncio);
                    registrarAnuncioCriado();
                    return AnuncioResponseDto.fromAnuncio(anuncioCriado);
                });
    }

    private void registrarAnuncioCriado() {
        long anunciosAtuais = anuncioRepository.count();
        Estatistica estatistica = estatisticaRepository
                .findById(Estatistica.ID_PRINCIPAL)
                .orElseGet(() -> {
                    Estatistica novaEstatistica = new Estatistica();
                    novaEstatistica.setTotalAnunciosCriados(anunciosAtuais);
                    return novaEstatistica;
                });

        if (estatisticaRepository.existsById(Estatistica.ID_PRINCIPAL)) {
            estatistica.setTotalAnunciosCriados(
                    Math.max(estatistica.getTotalAnunciosCriados() + 1, anunciosAtuais)
            );
        }

        estatisticaRepository.save(estatistica);
    }

    @Transactional(readOnly = true)
    public boolean atingiuLimiteAnuncios(Long usuarioId) {
        return usuarioId != null
                && anuncioRepository.countByUsuarioId(usuarioId)
                >= LIMITE_ANUNCIOS_POR_USUARIO;
    }

    public PaginaResponseDto<AnuncioResponseDto> listar(
            String titulo,
            java.util.List<CategoriaAnuncio> categorias,
            int pagina,
            int tamanho
    ) {
        String tituloNormalizado = titulo == null ? "" : titulo.trim();
        boolean possuiTitulo = !tituloNormalizado.isEmpty();
        boolean possuiCategorias = categorias != null && !categorias.isEmpty();
        PageRequest pageable = PageRequest.of(pagina, tamanho);
        Page<Anuncio> anuncios;

        if (possuiTitulo && possuiCategorias) {
            anuncios = anuncioRepository.findByStatusAndTituloContainingIgnoreCaseAndCategoriaInOrderByIdDesc(
                    StatusAnuncio.DISPONIVEL, tituloNormalizado, categorias, pageable);
        } else if (possuiTitulo) {
            anuncios = anuncioRepository.findByStatusAndTituloContainingIgnoreCaseOrderByIdDesc(
                    StatusAnuncio.DISPONIVEL, tituloNormalizado, pageable);
        } else if (possuiCategorias) {
            anuncios = anuncioRepository.findByStatusAndCategoriaInOrderByIdDesc(
                    StatusAnuncio.DISPONIVEL, categorias, pageable);
        } else {
            anuncios = anuncioRepository.findByStatusOrderByIdDesc(StatusAnuncio.DISPONIVEL, pageable);
        }

        return PaginaResponseDto.fromPage(anuncios.map(AnuncioResponseDto::fromAnuncio));
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
                .filter(anuncio -> anuncio.getStatus() == StatusAnuncio.DISPONIVEL)
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

    @Transactional
    public Optional<AnuncioResponseDto> encerrar(Long anuncioId, StatusAnuncio status) {
        if (status != StatusAnuncio.VENDIDO && status != StatusAnuncio.DOADO) {
            return Optional.empty();
        }

        return anuncioRepository.findById(anuncioId)
                .filter(anuncio -> statusCompativelComTipo(anuncio.getTipo(), status))
                .map(anuncio -> {
                    anuncio.setStatus(status);
                    return AnuncioResponseDto.fromAnuncio(anuncioRepository.save(anuncio));
                });
    }

    private boolean statusCompativelComTipo(TipoAnuncio tipo, StatusAnuncio status) {
        return (tipo == TipoAnuncio.VENDA && status == StatusAnuncio.VENDIDO)
                || (tipo == TipoAnuncio.DOACAO && status == StatusAnuncio.DOADO);
    }

    @Transactional(readOnly = true)
    public Optional<Boolean> verificarInteresse(Long anuncioId, Long usuarioId) {
        return anuncioRepository.findById(anuncioId)
                .flatMap(anuncio -> userRepository.findById(usuarioId)
                        .map(usuario -> usuario.getItensInteressados().contains(anuncio)));
    }

}
