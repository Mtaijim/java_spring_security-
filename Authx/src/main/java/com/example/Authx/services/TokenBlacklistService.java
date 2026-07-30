package com.example.Authx.services;

import com.example.Authx.entity.BlackListedToken;
import com.example.Authx.repositories.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public void blacklist(String jti , Instant expiresAt){

        if(blacklistedTokenRepository.existsByJti(jti)){
            return;
        }

        BlackListedToken token  = BlackListedToken.builder()
                .jti(jti)
                .expiresAt(expiresAt).build();


        blacklistedTokenRepository.save(token);
        log.info("Token blacklisted: {} ",jti);

    }

    public boolean isBlacklisted(String jti){
        return blacklistedTokenRepository.existsByJti(jti);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredTokens(){
        blacklistedTokenRepository.deleteExpiredTokens(
                Instant.now()
        );
        log.info("Cleaned up expired blacklisted tokens");
    }

}
