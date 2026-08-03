package com.luizing.marktplaceCircular.service;

import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.dtos.LoginResponseDto;
import com.luizing.marktplaceCircular.dtos.PaginaResponseDto;
import com.luizing.marktplaceCircular.dtos.UserContatoDto;
import com.luizing.marktplaceCircular.dtos.UserDto;
import com.luizing.marktplaceCircular.dtos.UserLoginDto;
import com.luizing.marktplaceCircular.dtos.UserResponseDto;
import com.luizing.marktplaceCircular.model.anuncio.Anuncio;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.model.user.User;
import com.luizing.marktplaceCircular.repository.AnuncioRepository;
import com.luizing.marktplaceCircular.repository.UserRepository;
import com.luizing.marktplaceCircular.security.JwtService;
import com.luizing.marktplaceCircular.validation.ValidacaoDados;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AnuncioRepository anuncioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            AnuncioRepository anuncioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.anuncioRepository = anuncioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Optional<UserResponseDto> criar(UserDto dto) {
        ValidacaoDados.validarNovoUsuario(dto);

        if (userRepository.existsByLogin(dto.login())) {
            return Optional.empty();
        }

        User usuario = dto.toUser();
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        User userCriado = userRepository.save(usuario);
        return Optional.of(UserResponseDto.fromUser(userCriado));
    }

    public Optional<LoginResponseDto> verificarLogin(UserLoginDto dto) {
        ValidacaoDados.validarLogin(dto);

        return userRepository.findByLogin(dto.login())
                .filter(usuario -> senhaValida(usuario, dto.senha()))
                .map(usuario -> LoginResponseDto.fromUser(
                        usuario, jwtService.gerarToken(usuario.getLogin())));
    }

    public boolean usuarioEhAutenticado(Long usuarioId, String login) {
        return login != null && userRepository.findById(usuarioId)
                .map(usuario -> usuario.getLogin().equals(login))
                .orElse(false);
    }

    public Optional<UserContatoDto> retornarContato(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserContatoDto(user.getId(), user.getLogin(), user.getContato()));
    }

    public Optional<PaginaResponseDto<AnuncioResponseDto>> retornarItensAnunciados(
            Long id,
            String titulo,
            List<CategoriaAnuncio> categorias,
            int pagina,
            int tamanho
    ) {
        if (!userRepository.existsById(id)) {
            return Optional.empty();
        }

        return Optional.of(PaginaResponseDto.fromPage(buscarAnunciosDoUsuario(
                id, titulo, categorias, PageRequest.of(pagina, tamanho)
        ).map(AnuncioResponseDto::fromAnuncio)));
    }

    public Optional<PaginaResponseDto<AnuncioResponseDto>> retornarItensInteressados(
            Long id,
            String titulo,
            List<CategoriaAnuncio> categorias,
            int pagina,
            int tamanho
    ) {
        if (!userRepository.existsById(id)) {
            return Optional.empty();
        }

        return Optional.of(PaginaResponseDto.fromPage(buscarAnunciosInteressantes(
                id, titulo, categorias, PageRequest.of(pagina, tamanho)
        ).map(AnuncioResponseDto::fromAnuncio)));
    }

    private Page<Anuncio> buscarAnunciosDoUsuario(
            Long usuarioId,
            String titulo,
            List<CategoriaAnuncio> categorias,
            PageRequest pageable
    ) {
        String tituloNormalizado = titulo == null ? "" : titulo.trim();
        boolean possuiTitulo = !tituloNormalizado.isEmpty();
        boolean possuiCategorias = categorias != null && !categorias.isEmpty();

        if (possuiTitulo && possuiCategorias) {
            return anuncioRepository.findByUsuarioIdAndTituloContainingIgnoreCaseAndCategoriaInOrderByIdDesc(
                    usuarioId, tituloNormalizado, categorias, pageable);
        }
        if (possuiTitulo) {
            return anuncioRepository.findByUsuarioIdAndTituloContainingIgnoreCaseOrderByIdDesc(
                    usuarioId, tituloNormalizado, pageable);
        }
        if (possuiCategorias) {
            return anuncioRepository.findByUsuarioIdAndCategoriaInOrderByIdDesc(
                    usuarioId, categorias, pageable);
        }
        return anuncioRepository.findByUsuarioIdOrderByIdDesc(usuarioId, pageable);
    }

    private Page<Anuncio> buscarAnunciosInteressantes(
            Long usuarioId,
            String titulo,
            List<CategoriaAnuncio> categorias,
            PageRequest pageable
    ) {
        String tituloNormalizado = titulo == null ? "" : titulo.trim();
        boolean possuiTitulo = !tituloNormalizado.isEmpty();
        boolean possuiCategorias = categorias != null && !categorias.isEmpty();

        if (possuiTitulo && possuiCategorias) {
            return anuncioRepository.findInteressantesByUsuarioIdAndTituloAndCategorias(
                    usuarioId, tituloNormalizado, categorias, pageable);
        }
        if (possuiTitulo) {
            return anuncioRepository.findInteressantesByUsuarioIdAndTitulo(
                    usuarioId, tituloNormalizado, pageable);
        }
        if (possuiCategorias) {
            return anuncioRepository.findInteressantesByUsuarioIdAndCategorias(
                    usuarioId, categorias, pageable);
        }
        return anuncioRepository.findInteressantesByUsuarioId(usuarioId, pageable);
    }

    private boolean senhaValida(User usuario, String senha) {
        if (senha == null) {
            return false;
        }

        if (passwordEncoder.matches(senha, usuario.getSenha())) {
            return true;
        }

        if (senha.equals(usuario.getSenha())) {
            usuario.setSenha(passwordEncoder.encode(senha));
            userRepository.save(usuario);
            return true;
        }

        return false;
    }
}
