package com.luizing.marktplaceCircular.controller;

import com.luizing.marktplaceCircular.dtos.AnuncioDto;
import com.luizing.marktplaceCircular.dtos.AtualizarStatusAnuncioDto;
import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.dtos.ApiErroDto;
import com.luizing.marktplaceCircular.dtos.PaginaResponseDto;
import com.luizing.marktplaceCircular.dtos.UserContatoDto;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.model.anuncio.StatusAnuncio;
import com.luizing.marktplaceCircular.service.AnuncioService;
import com.luizing.marktplaceCircular.service.UserService;
import com.luizing.marktplaceCircular.validation.ValidacaoDados;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/anuncios")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://market-place-circular.vercel.app"
})
public class AnuncioController {

    private final AnuncioService anuncioService;
    private final UserService userService;

    public AnuncioController(AnuncioService anuncioService, UserService userService) {
        this.anuncioService = anuncioService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @RequestBody AnuncioDto dto,
            Authentication authentication
    ) {
        ValidacaoDados.validarAnuncio(dto);

        if (!usuarioEhAutenticado(dto.usuarioId(), authentication)) {
            return erro(HttpStatus.FORBIDDEN, "Voce nao pode criar anuncios em nome de outro usuario.");
        }

        if (anuncioService.atingiuLimiteAnuncios(dto.usuarioId())) {
            return erro(HttpStatus.CONFLICT, "Cada usuario pode criar no maximo 3 anuncios.");
        }

        return anuncioService.criar(dto)
                .<ResponseEntity<?>>map(anuncio -> ResponseEntity.status(HttpStatus.CREATED).body(anuncio))
                .orElseGet(() -> erro(HttpStatus.BAD_REQUEST, "Nao foi possivel criar o anuncio."));
    }

    @GetMapping
    public ResponseEntity<PaginaResponseDto<AnuncioResponseDto>> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) List<CategoriaAnuncio> categoria,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho
    ) {
        if (pagina < 0 || tamanho < 1 || tamanho > 50) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(anuncioService.listar(titulo, categoria, pagina, tamanho));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnuncioResponseDto> buscarPorId(@PathVariable Long id) {
        return anuncioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            Authentication authentication
    ) {
        if (!usuarioEhAutenticado(usuarioId, authentication)) {
            return erro(HttpStatus.FORBIDDEN, "Voce nao pode apagar anuncios de outro usuario.");
        }

        if (!anuncioService.usuarioEhDono(id, usuarioId)) {
            return anuncioService.buscarPorId(id).isPresent()
                    ? erro(HttpStatus.FORBIDDEN, "Apenas o dono pode apagar este anuncio.")
                    : erro(HttpStatus.NOT_FOUND, "Anuncio nao encontrado.");
        }

        boolean anuncioRemovido = anuncioService.deletar(id);

        if (!anuncioRemovido) {
            return erro(HttpStatus.NOT_FOUND, "Anuncio nao encontrado.");
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> encerrar(
            @PathVariable Long id,
            @RequestBody AtualizarStatusAnuncioDto dto,
            Authentication authentication
    ) {
        if (dto == null || dto.status() == null) {
            return erro(HttpStatus.BAD_REQUEST, "Informe o status final do anuncio.");
        }

        Long usuarioId = authentication == null
                ? null
                : userService.buscarIdPorLogin(authentication.getName()).orElse(null);

        if (usuarioId == null) {
            return erro(HttpStatus.FORBIDDEN, "Autenticacao invalida. Faca login novamente.");
        }

        if (!anuncioService.usuarioEhDono(id, usuarioId)) {
            return anuncioService.buscarPorId(id).isPresent()
                    ? erro(HttpStatus.FORBIDDEN, "Apenas o dono pode encerrar este anuncio.")
                    : erro(HttpStatus.NOT_FOUND, "Anuncio nao encontrado.");
        }

        return anuncioService.encerrar(id, dto.status())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> erro(HttpStatus.BAD_REQUEST,
                        "O status final deve corresponder ao tipo do anuncio."));
    }

    @GetMapping("/{anuncioId}/interessados")
    public ResponseEntity<List<UserContatoDto>> retornarInteressados(
            @PathVariable Long anuncioId,
            @RequestParam Long usuarioId,
            Authentication authentication
    ) {
        if (!usuarioEhAutenticado(usuarioId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!anuncioService.usuarioEhDono(anuncioId, usuarioId)) {
            return anuncioService.buscarPorId(anuncioId).isPresent()
                    ? ResponseEntity.status(HttpStatus.FORBIDDEN).build()
                    : ResponseEntity.notFound().build();
        }

        return anuncioService.retornarInteressados(anuncioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{anuncioId}/interessados/{usuarioId}")
    public ResponseEntity<AnuncioResponseDto> interessar(
            @PathVariable Long anuncioId,
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {
        if (!usuarioEhAutenticado(usuarioId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var anuncio = anuncioService.buscarPorId(anuncioId);

        if (anuncio.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (anuncio.get().status() != StatusAnuncio.DISPONIVEL) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (anuncioService.usuarioEhDono(anuncioId, usuarioId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return anuncioService.interessar(anuncioId, usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{anuncioId}/interessados/{usuarioId}")
    public ResponseEntity<AnuncioResponseDto> desinteressar(
            @PathVariable Long anuncioId,
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {
        if (!usuarioEhAutenticado(usuarioId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return anuncioService.desinteressar(anuncioId, usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{anuncioId}/interessados/{usuarioId}")
    public ResponseEntity<Boolean> verificarInteresse(
            @PathVariable Long anuncioId,
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {
        if (!usuarioEhAutenticado(usuarioId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return anuncioService.verificarInteresse(anuncioId, usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private boolean usuarioEhAutenticado(Long usuarioId, Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && userService.usuarioEhAutenticado(usuarioId, authentication.getName());
    }

    private ResponseEntity<ApiErroDto> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new ApiErroDto(mensagem));
    }
}
