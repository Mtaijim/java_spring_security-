package com.example.Authx.dtos;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        UserDto user,
        boolean mfaRequired,
        String mfaToken,
        boolean requiresVerification   // fixed typo
) {

    public static TokenResponse of(String accessToken, String refreshToken,
                                   long expiresIn, UserDto user) {
        return new TokenResponse(
                accessToken, refreshToken, expiresIn, "Bearer",
                user, false, null, false
        );
    }

    public static TokenResponse mfaRequired(String mfaToken) {
        return new TokenResponse(
                null, null, 0, "Bearer",
                null, true, mfaToken, false
        );
    }

    public static TokenResponse verificationRequired() {
        return new TokenResponse(
                null, null, 0, "Bearer",
                null, false, null, true
        );
    }
}