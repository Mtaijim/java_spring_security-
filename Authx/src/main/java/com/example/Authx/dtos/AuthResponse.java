package com.example.Authx.dtos;

import com.example.Authx.entity.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private UserDto user;
    private boolean mfaRequired;
    private String email;
    private RiskLevel riskLevel;
    private boolean requiresVerification;
    private String message;
}
