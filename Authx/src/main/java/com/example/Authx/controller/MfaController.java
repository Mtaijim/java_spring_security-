package com.example.Authx.controller;

import com.example.Authx.dtos.TokenResponse;
import com.example.Authx.dtos.mfa.BackUpcodesResponse;
import com.example.Authx.dtos.mfa.MfaCodeRequest;
import com.example.Authx.dtos.mfa.MfaSetupResponse;
import com.example.Authx.entity.User;
import com.example.Authx.helper.UserHelper;
import com.example.Authx.repositories.userRepository;
import com.example.Authx.security.JwtService;
import com.example.Authx.services.BackupCodeService;
import com.example.Authx.services.TokenIssuanceService;
import com.example.Authx.services.TotpService;
import dev.samstevens.totp.exceptions.QrGenerationException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/mfa")
@RequiredArgsConstructor
public class MfaController {
    private final TotpService totpService;
    private final BackupCodeService backupCodeService;
    private final userRepository userRepository;
    private final JwtService jwtService;
    private final TokenIssuanceService tokenIssuanceService;


    private User getCurrentUser(Authentication authentication){
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("user not Found"));

    }

    @GetMapping("/setup")
    public MfaSetupResponse setUp(Authentication authentication) throws QrGenerationException {
        User user = getCurrentUser(authentication);
        String secret = totpService.generateSecret();
        user.setTotpSecret(secret);
        userRepository.save(user);

        String qrcode = totpService.generateQrCode(secret, user.getEmail());
        return new MfaSetupResponse(secret,qrcode);

    }

    @PostMapping("/verify-setup")
    public BackUpcodesResponse verifySetup(Authentication authentication,@RequestBody MfaCodeRequest request ){
        User user = getCurrentUser(authentication);
        if (!totpService.verifyCode(user.getTotpSecret(), request.getCode())) {
            throw new IllegalArgumentException("Invalid code. Please try again.");
        }
        user.setMfaEnabled(true);
        userRepository.save(user);

        List<String> codes  = backupCodeService.generateBackUpCodes(user);
        return new BackUpcodesResponse(codes);
    }

    @PostMapping("/disable")
    public Map<String, String> disable(Authentication authentication, @RequestBody MfaCodeRequest request){
        User user = getCurrentUser(authentication);
        if(!totpService.verifyCode(user.getTotpSecret(),request.getCode())){
            throw  new IllegalArgumentException("Invalid code");

        }
        user.setMfaEnabled(false);
        user.setTotpSecret(null);
        userRepository.save(user);
        return Map.of("message","MFA disabled successfully");
    }
    @PostMapping("/status")
    public Map<String,Object> status(Authentication authentication){
        User user = getCurrentUser(authentication);
        return Map.of(
                "mfaEnabled",user.isMfaEnabled(),
                "backUpCodesRemaining",user.isMfaEnabled() ?backupCodeService.countRemaining(user):0
        );
    }

    @PostMapping("/backup/regenerate")
    public BackUpcodesResponse regenerateBackupCodes(Authentication authentication,@RequestBody MfaCodeRequest request){
        User user = getCurrentUser(authentication);
        if (!totpService.verifyCode(user.getTotpSecret(), request.getCode())) {
            throw new IllegalArgumentException("Invalid code.");
        }
        List<String> codes = backupCodeService.generateBackUpCodes(user);
        return new BackUpcodesResponse(codes);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenResponse> validate(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody MfaCodeRequest request,
                                                  HttpServletResponse response) {
        User user = resolveUserFromMfaToken(authHeader);

        if (!totpService.verifyCode(user.getTotpSecret(), request.getCode())) {
            throw new IllegalArgumentException("Invalid authentication code");
        }

        return ResponseEntity.ok(tokenIssuanceService.issuesToken(user, response));
    }

    @PostMapping("/backup/verify")
    public ResponseEntity<TokenResponse> verifyBackupCode(@RequestHeader("Authorization") String authHeader,
                                                          @RequestBody MfaCodeRequest request,
                                                          HttpServletResponse response) {
        User user = resolveUserFromMfaToken(authHeader);

        if (!backupCodeService.verifyAndConsume(user, request.getCode())) {
            throw new IllegalArgumentException("Invalid or already-used backup code");
        }

        return ResponseEntity.ok(tokenIssuanceService.issuesToken(user, response));
    }



    private User resolveUserFromMfaToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing MFA token");
        }
        String token = authHeader.substring(7);

        if (!jwtService.isMfaToken(token)) {
            throw new IllegalArgumentException("Invalid or expired MFA session. Please log in again.");
        }

        UUID userId = UserHelper.parseUUID(jwtService.parse(token).getPayload().getSubject());
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
