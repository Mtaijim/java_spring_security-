package com.example.Authx.controller;

import com.example.Authx.dtos.*;
import com.example.Authx.entity.*;
import com.example.Authx.helper.DeviceParser;
import com.example.Authx.repositories.RiskScoreRepository;
import com.example.Authx.repositories.RiskVerificationTokenRepository;
import com.example.Authx.repositories.userRepository;
import com.example.Authx.security.JwtService;
import com.example.Authx.services.*;
import io.github.bucket4j.Bucket;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.Authx.repositories.RefreshTokenRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
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
    private final TokenIssuanceService tokenIssuanceService;
    private final LoginEventServices loginEventServices;
    private final AccountLockoutService lockoutService;
    private final TokenBlacklistService tokenBlacklistService;
    private final SuspiciousLoginService suspiciousLoginService;
    private final DeviceParser deviceParser;
    private final RateLimitService rateLimitService;
    private  final  AuditLogService auditLogService;
    private final EmailService emailService;
    private final RiskScoringService riskScoringService;
    private final RiskScoreRepository riskScoreRepository;
    private final RiskVerificationTokenRepository riskVerificationTokenRepository;


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response, HttpServletRequest request
    ) {
        User user = null;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(),
                            loginRequest.password())
            );
            user = userRepository.findByEmail(loginRequest.email())
                            .orElseThrow(() ->
                    new BadCredentialsException("invalid credentials"));

            if (!user.isEnabled()) {
                throw new DisabledException("user is Disabled ");

            }
            if (user.isMfaEnabled()) {
                String mfaToken = jwtService.generateMfaToken(user.getId().toString());
                return ResponseEntity.ok(TokenResponse.mfaRequired(mfaToken));
            }

//            new changes made
            String ip = deviceParser.parseIp(request);
            String agent = request.getHeader("User-Agent");
            RiskScore risk = riskScoringService.calculateRisk(user, ip, agent);
            riskScoreRepository.save(risk);
            if(risk.getLevel() == RiskLevel.HIGH){
                String otp = String.valueOf((int) (100000 + Math.random() * 900000));

                RiskVerificationToken token = RiskVerificationToken.builder()
                        .token(otp)
                        .user(user)
                        .used(false)
                        .expiresAt(Instant.now().plusSeconds(300))
                        .build();

                riskVerificationTokenRepository.save(token);
                emailService.sendRiskAlertEmail(user.getEmail(), otp);
                return ResponseEntity.ok(
                        TokenResponse.verificationRequired()
                );
            }


//            record success
           lockoutService.handleSuccess(user);
            loginEventServices.recordSucess(user, request);
            auditLogService.log(
                    null,
                    user.getId(),
                    user.getEmail(),
                    AuditAction.LOGIN,
                    "AUTH",
                    null,
                    "User logged in successfully",
                    deviceParser.parseIp(request),
                    request.getHeader("User-Agent")
            );
            // @Async means this runs without blocking response
            suspiciousLoginService.checkAndAlert(
                    user,
                    deviceParser.parseDevice(request),
                    deviceParser.parseOs(request),
                    deviceParser.parseIp(request)
            );
            return ResponseEntity.ok(tokenIssuanceService.issuesToken(user, response));


        } catch (BadCredentialsException | DisabledException e) {
            if (user == null) {
                user = userRepository
                        .findByEmail(loginRequest.email())
                        .orElse(null);
            }
            if (user != null) {

                lockoutService.handleFailedAttempt(user);
                loginEventServices.recordFailure(user,
                        request, e.getMessage());
                auditLogService.logFailure(
                        null,
                        user.getId(),
                        user.getEmail(),
                        AuditAction.LOGIN_FAILED,
                        "AUTH",
                        null,
                        "Login failed: " + e.getMessage(),
                        deviceParser.parseIp(request),
                        request.getHeader("User-Agent")
                );
                int remaining = lockoutService.remainingAttempts(user);
                if(remaining>0){
                    throw new BadCredentialsException(
                            "Invalid password. "+
                                    remaining +" attempt" +
                                    (remaining>1 ? "s" : "") + " remaining."
                    );
                }else {
                    throw new BadCredentialsException(
                            "Account locked for 15 min"
                    );
                }
            }
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
//        blacklist the access token
    String authHeader = request.getHeader("Authorization");
    if(authHeader != null && authHeader.startsWith("Bearer ")){
        String accessToken = authHeader.substring(7);
        try{
            if(jwtService.isAccessToken(accessToken)){
                String jti = jwtService.getJti(accessToken);


//                get expiry from token

                Instant expiresAt = jwtService
                        .parse(accessToken)
                        .getPayload()
                        .getExpiration()
                        .toInstant();

//                add to blacklist
tokenBlacklistService.blacklist(jti,expiresAt);


                UUID userId = jwtService.getUserId(accessToken);
                String email = jwtService.getEmail(accessToken);

                auditLogService.log(
                        null,
                        userId,
                        email,
                        AuditAction.LOGOUT,
                        "AUTH",
                        null,
                        "User logged out",
                        deviceParser.parseIp(request),
                        request.getHeader("User-Agent")
                );

            }
        }catch (Exception ignored){}
    }



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
    SecurityContextHolder.clearContext();

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
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        Bucket bucket = rateLimitService.getRegisterBucket(userDto.getEmail());
        if (!rateLimitService.tryConsume(bucket)) {
            Map<String, Object> body = Map.of(
                    "status", 429,
                    "message", "Too many registration attempts. Please wait 1 hour.",
                    "remainingAttempts", rateLimitService.remainingTokens(bucket)
            );
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(body);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token){
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully . you can now login");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String,String> body){
        String email = body.get("email");
        Bucket bucket= rateLimitService.getForgetPasswordBucket(email);

        if(!rateLimitService.tryConsume(bucket)){
            Map<String, Object> resp = Map.of(
                    "status", 429,
                    "message", "Too many password reset requests. Please wait 1 hour.",
                    "remainingAttempts", rateLimitService.remainingTokens(bucket)
            );
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(resp);

        }

        authService.forgotPassword(email);
        return ResponseEntity.ok("If that email exists , a reset link has been sent .");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        authService.resetPassword(body.get("token"), body.get("newPassword"));
        return ResponseEntity.ok("Password reset successfully. You can now login.");
    }

    @PostMapping("/verify-risk")
    public ResponseEntity<AuthResponse> verifyRisk(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                authService.verifyRiskOtp(body.get("email"), body.get("otp"))
        );
    }

}