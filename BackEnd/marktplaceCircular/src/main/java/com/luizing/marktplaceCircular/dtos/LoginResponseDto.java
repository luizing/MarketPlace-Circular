package com.luizing.marktplaceCircular.dtos;

import com.luizing.marktplaceCircular.model.user.User;

public record LoginResponseDto(
        Long id,
        String login,
        String contato,
        String token
) {

    public static LoginResponseDto fromUser(User user, String token) {
        return new LoginResponseDto(user.getId(), user.getLogin(), user.getContato(), token);
    }
}
