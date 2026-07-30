package com.luizing.marktplaceCircular.service;

import com.luizing.marktplaceCircular.dtos.AnuncioResponseDto;
import com.luizing.marktplaceCircular.dtos.UserContatoDto;
import com.luizing.marktplaceCircular.dtos.UserDto;
import com.luizing.marktplaceCircular.dtos.UserLoginDto;
import com.luizing.marktplaceCircular.dtos.UserResponseDto;
import com.luizing.marktplaceCircular.model.user.User;
import com.luizing.marktplaceCircular.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserResponseDto> criar(UserDto dto) {
        if (userRepository.existsByLogin(dto.login())) {
            return Optional.empty();
        }

        User userCriado = userRepository.save(dto.toUser());
        return Optional.of(UserResponseDto.fromUser(userCriado));
    }

    public Optional<UserResponseDto> verificarLogin(UserLoginDto dto) {
        return userRepository.findByLoginAndSenha(dto.login(), dto.senha())
                .map(UserResponseDto::fromUser);
    }

    public Optional<UserContatoDto> retornarContato(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserContatoDto(user.getId(), user.getLogin(), user.getContato()));
    }

    public Optional<List<AnuncioResponseDto>> retornarItensAnunciados(Long id) {
        return userRepository.findById(id)
                .map(user -> user.getItensAnunciados().stream()
                        .map(AnuncioResponseDto::fromAnuncio)
                        .toList());
    }

    public Optional<List<AnuncioResponseDto>> retornarItensInteressados(Long id) {
        return userRepository.findById(id)
                .map(user -> user.getItensInteressados().stream()
                        .map(AnuncioResponseDto::fromAnuncio)
                        .toList());
    }
}
