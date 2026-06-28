package com.example.Authx.repositories;

import com.example.Authx.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken,String> {
    Optional<EmailVerificationToken> findByToken(String token);
    void deleteByUser(com.example.Authx.entity.User user);

}
