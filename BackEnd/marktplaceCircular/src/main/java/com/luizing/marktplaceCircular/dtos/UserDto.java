package com.luizing.marktplaceCircular.dtos;

import com.luizing.marktplaceCircular.model.user.User;

public record UserDto(
        String login,
        String senha,
        String contato
) {

    public User toUser() {
        User user = new User();
        user.setLogin(login);
        user.setSenha(senha);
        user.setContato(contato);

        return user;
    }
}
