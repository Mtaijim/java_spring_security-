package com.example.Authx.services;

import com.example.Authx.dtos.TokenResponse;
import com.example.Authx.dtos.UserDto;
import com.example.Authx.entity.RefreshToken;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.RefreshTokenRepository;
import com.example.Authx.security.CookieService;
import com.example.Authx.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenIssuanceService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final ModelMapper modelMapper;

    public TokenResponse issuesToken(User user , HttpServletResponse response){
        String jti = UUID.randomUUID().toString();
        var refreshToken = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshtoken = jwtService.generateRefreshToken(user, refreshToken.getJti());

        cookieService.attachRefreshCookie(response, refreshtoken, (int) jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeader(response);

        return TokenResponse.of(accessToken, refreshtoken, jwtService.getAccessTtlSeconds(),modelMapper.map(user, UserDto.class));

    }
}
