package com.luizing.marktplaceCircular.controller;

import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.dtos.ApiErroDto;
import com.luizing.marktplaceCircular.dtos.PaginaResponseDto;
import com.luizing.marktplaceCircular.dtos.UserContatoDto;
import com.luizing.marktplaceCircular.dtos.UserDto;
import com.luizing.marktplaceCircular.dtos.UserLoginDto;
import com.luizing.marktplaceCircular.dtos.LoginResponseDto;
import com.luizing.marktplaceCircular.dtos.UserResponseDto;
import com.luizing.marktplaceCircular.model.anuncio.CategoriaAnuncio;
import com.luizing.marktplaceCircular.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://market-place-circular.vercel.app"
})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody UserDto dto) {
        return userService.criar(dto)
                .<ResponseEntity<?>>map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user))
                .orElseGet(() -> erro(HttpStatus.CONFLICT, "Este login ja esta cadastrado."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> verificarLogin(@RequestBody UserLoginDto dto) {
        return userService.verificarLogin(dto)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> erro(HttpStatus.UNAUTHORIZED, "Login ou senha invalidos."));
    }

    @GetMapping("/{id}/contato")
    public ResponseEntity<UserContatoDto> retornarContato(@PathVariable Long id) {
        return userService.retornarContato(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/anuncios")
    public ResponseEntity<PaginaResponseDto<AnuncioResponseDto>> retornarItensAnunciados(
            @PathVariable Long id,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) List<CategoriaAnuncio> categoria,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho,
            Authentication authentication
    ) {
        if (!usuarioEhAutenticado(id, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (pagina < 0 || tamanho < 1 || tamanho > 50) {
            return ResponseEntity.badRequest().build();
        }

        return userService.retornarItensAnunciados(id, titulo, categoria, pagina, tamanho)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/interessados")
    public ResponseEntity<PaginaResponseDto<AnuncioResponseDto>> retornarItensInteressados(
            @PathVariable Long id,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) List<CategoriaAnuncio> categoria,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho,
            Authentication authentication
    ) {
        if (!usuarioEhAutenticado(id, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (pagina < 0 || tamanho < 1 || tamanho > 50) {
            return ResponseEntity.badRequest().build();
        }

        return userService.retornarItensInteressados(id, titulo, categoria, pagina, tamanho)
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
