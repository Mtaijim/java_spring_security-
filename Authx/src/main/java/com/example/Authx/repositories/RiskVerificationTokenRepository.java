package com.example.Authx.repositories;

import com.example.Authx.entity.RiskVerificationToken;
import com.example.Authx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RiskVerificationTokenRepository extends JpaRepository<RiskVerificationToken, UUID> {


    Optional<RiskVerificationToken> findByUserAndTokenAndUsedFalse(User user, String token);
}
