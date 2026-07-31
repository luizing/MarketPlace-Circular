package com.luizing.marktplaceCircular.controller;

import com.luizing.marktplaceCircular.dtos.AnuncioDto;
import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.dtos.PaginaResponseDto;
import com.luizing.marktplaceCircular.dtos.UserContatoDto;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.service.AnuncioService;
import com.luizing.marktplaceCircular.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<AnuncioResponseDto> criar(
            @RequestBody AnuncioDto dto,
            Authentication authentication
    ) {
        if (!usuarioEhAutenticado(dto.usuarioId(), authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (anuncioService.atingiuLimiteAnuncios(dto.usuarioId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return anuncioService.criar(dto)
                .map(anuncio -> ResponseEntity.status(HttpStatus.CREATED).body(anuncio))
                .orElseGet(() -> ResponseEntity.badRequest().build());
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
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            Authentication authentication
    ) {
        if (!usuarioEhAutenticado(usuarioId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (!anuncioService.usuarioEhDono(id, usuarioId)) {
            return anuncioService.buscarPorId(id).isPresent()
                    ? ResponseEntity.status(HttpStatus.FORBIDDEN).build()
                    : ResponseEntity.notFound().build();
        }

        boolean anuncioRemovido = anuncioService.deletar(id);

        if (!anuncioRemovido) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
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
}
