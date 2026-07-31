package com.luizing.marktplaceCircular.repository;

import com.luizing.marktplaceCircular.model.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginAndSenha(String login, String senha);

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);
}
