package com.example.Authx.repositories;

import com.example.Authx.entity.BlackListedToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface BlacklistedTokenRepository extends JpaRepository<BlackListedToken,Long> {
    // check if a token is blacklisted
    boolean existsByJti(String jti);

    @Modifying
    @Transactional
    @Query("DELETE FROM BlacklistedToken b WHERE b.expiresAt < :now")
    void deleteExpiredTokens(Instant now);

}
