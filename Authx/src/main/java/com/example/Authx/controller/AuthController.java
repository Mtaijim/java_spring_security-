package com.example.Authx.controller;

import com.example.Authx.dtos.LoginRequest;
import com.example.Authx.dtos.RefreshTokenRequest;
import com.example.Authx.dtos.TokenResponse;
import com.example.Authx.dtos.UserDto;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.userRepository;
import com.example.Authx.security.JwtService;
import com.example.Authx.services.AuthService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Authx.entity.RefreshToken;
import com.example.Authx.repositories.RefreshTokenRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import com.example.Authx.security.CookieService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {


    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final userRepository userRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response

    ) {

        Authentication authenticate = authenticate(loginRequest);
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new BadCredentialsException("Invalid Username or Password"));

        if (!user.isEnable()) {
            throw new DisabledException("user is disabled");
        }
        String jti = UUID.randomUUID().toString();
        var refreshToken = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
// generate token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshtoken = jwtService.generateRefreshToken(user, refreshToken.getJti());

// use cookie service to attach refresh token to response
        cookieService.attachRefreshCookie(response, refreshtoken, (int) jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeader(response);


        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshtoken, jwtService.getAccessTtlSeconds(), modelMapper.map(user, UserDto.class));
        return ResponseEntity.ok(tokenResponse);
    }


    private Authentication authenticate(LoginRequest loginRequest) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );
        } catch (Exception e) {
            e.printStackTrace(); // IMPORTANT
            throw e;
        }
    }


    //access and refresh token regeneration endpoint
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody(required = false)
            RefreshTokenRequest body,
            HttpServletResponse response, HttpServletRequest request) {


        String refreshToken = readRefreshTokenFromRequest(body, request).orElseThrow(() -> new BadCredentialsException("invalid Refresh token"));
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("invalid refresh token type");
        }
      String jti =   jwtService.getJti(refreshToken);
      UUID userid = jwtService.getUserId(refreshToken);
        RefreshToken storedRefreshToken =  refreshTokenRepository.findByJti(jti).orElseThrow(()-> new BadCredentialsException("refresh token not recognized "));
if(storedRefreshToken.isRevoked()){
    throw new BadCredentialsException("Refresh token is revoked");
}
        if(storedRefreshToken.getExpiresAt().isBefore(Instant.now())){
            throw new BadCredentialsException("Refresh token expired");
        }

        if(!storedRefreshToken.getUser().getId().equals(userid)){
            throw new BadCredentialsException("Refresh token does not belong to this user");
        }
        //refresh token ko rotate:
         storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(storedRefreshToken);
        User user = storedRefreshToken.getUser();
        var newRefreshTokenOb = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(newRefreshTokenOb);
        String newAccessToken = jwtService.generateAccessToken(user);
         String newRefreshToken = jwtService.generateRefreshToken(user,newRefreshTokenOb.getJti());


    cookieService.attachRefreshCookie(response,newRefreshToken, (int) jwtService.getRefreshTtlSeconds());
    cookieService.addNoStoreHeader(response);
    return ResponseEntity.ok(TokenResponse.of(newAccessToken,newRefreshToken, jwtService.getAccessTtlSeconds(), modelMapper.map(user,UserDto.class)));
    }

@PostMapping("/logout")
public ResponseEntity<Void> logout(
        HttpServletRequest request,
        HttpServletResponse response
){
        readRefreshTokenFromRequest(null,request).ifPresent(token -> {
            try{
                if (jwtService.isRefreshToken(token)){
                    String  jti  = jwtService.getJti(token);
                    refreshTokenRepository.findByJti(jti).ifPresent(rt->{
                        rt.setRevoked(true);
                        refreshTokenRepository.save(rt);
                    });
                }
            }catch (JwtException ignored ){

            }
        });
    cookieService.clearRefreshCookie(response);
    cookieService.addNoStoreHeader(response);
    SecurityContextHolder.clearContext();;

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
}

    // this method will read refresh token from request header or body
    private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest body, HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<String> fromCookie = Arrays.stream(
                            request.getCookies()
                    ).filter(cookie -> cookieService.getRefreshTokenCookieName().equals(cookie.getName())).map(Cookie::getValue)
                    .filter(v -> v != null && !v.isBlank()).findFirst();
            if (fromCookie.isPresent()) {
                return fromCookie;
            }
        }
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return Optional.of(body.refreshToken());

        }
        String refreshHeader = request.getHeader("X-Refresh-Token");
        if (refreshHeader != null && !refreshHeader.isBlank()) {
            return Optional.of(refreshHeader.trim());
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {

            try {
                String candidate = authHeader.substring(7).trim();
                if (jwtService.isRefreshToken(candidate)) {
                    return Optional.of(candidate);
                }
            } catch (Exception ignored) {
            }

        }
        return Optional.empty();
    }


    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }

}