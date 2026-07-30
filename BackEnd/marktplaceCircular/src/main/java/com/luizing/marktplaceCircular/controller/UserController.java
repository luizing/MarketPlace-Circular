package com.luizing.marktplaceCircular.controller;

import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.dtos.UserContatoDto;
import com.luizing.marktplaceCircular.dtos.UserDto;
import com.luizing.marktplaceCircular.dtos.UserLoginDto;
import com.luizing.marktplaceCircular.dtos.UserResponseDto;
import com.luizing.marktplaceCircular.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<UserResponseDto> criar(@RequestBody UserDto dto) {
        return userService.criar(dto)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> verificarLogin(@RequestBody UserLoginDto dto) {
        return userService.verificarLogin(dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/{id}/contato")
    public ResponseEntity<UserContatoDto> retornarContato(@PathVariable Long id) {
        return userService.retornarContato(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/anuncios")
    public ResponseEntity<List<AnuncioResponseDto>> retornarItensAnunciados(
            @PathVariable Long id
    ) {
        return userService.retornarItensAnunciados(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/interessados")
    public ResponseEntity<List<AnuncioResponseDto>> retornarItensInteressados(
            @PathVariable Long id
    ) {
        return userService.retornarItensInteressados(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
