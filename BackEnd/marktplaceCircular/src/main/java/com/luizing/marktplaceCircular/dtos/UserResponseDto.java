package com.luizing.marktplaceCircular.dtos;

import com.luizing.marktplaceCircular.model.user.User;

public record UserResponseDto(
        Long id,
        String login,
        String contato
) {

    public static UserResponseDto fromUser(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getLogin(),
                user.getContato()
        );
    }
}
