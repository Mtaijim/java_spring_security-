package com.example.Authx.repositories;

import com.example.Authx.entity.PasswordResetToken;
import com.example.Authx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,String> {

    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);

}
