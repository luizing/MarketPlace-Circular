package com.luizing.marktplaceCircular.controller;

import com.luizing.marktplaceCircular.dtos.AnuncioDto;
import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.service.AnuncioService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@CrossOrigin(origins = "http://localhost:5173")
public class AnuncioController {

    private final AnuncioService anuncioService;

    public AnuncioController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @PostMapping
    public ResponseEntity<AnuncioResponseDto> criar(@RequestBody AnuncioDto dto) {
        AnuncioResponseDto anuncioCriado = anuncioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(anuncioCriado);
    }

    @GetMapping
    public ResponseEntity<List<AnuncioResponseDto>> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) CategoriaAnuncio categoria
    ) {
        List<AnuncioResponseDto> anuncios = anuncioService.listar(titulo, categoria);
        return ResponseEntity.ok(anuncios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnuncioResponseDto> buscarPorId(@PathVariable Long id) {
        return anuncioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean anuncioRemovido = anuncioService.deletar(id);

        if (!anuncioRemovido) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{anuncioId}/interessados/{usuarioId}")
    public ResponseEntity<AnuncioResponseDto> interessar(
            @PathVariable Long anuncioId,
            @PathVariable Long usuarioId
    ) {
        return anuncioService.interessar(anuncioId, usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{anuncioId}/interessados/{usuarioId}")
    public ResponseEntity<AnuncioResponseDto> desinteressar(
            @PathVariable Long anuncioId,
            @PathVariable Long usuarioId
    ) {
        return anuncioService.desinteressar(anuncioId, usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{anuncioId}/interessados/{usuarioId}")
    public ResponseEntity<Boolean> verificarInteresse(
            @PathVariable Long anuncioId,
            @PathVariable Long usuarioId
    ) {
        return anuncioService.verificarInteresse(anuncioId, usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
