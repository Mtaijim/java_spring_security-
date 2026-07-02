package com.example.Authx.dtos.mfa;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MfaSetupResponse {
    private String secret;
    private String Qrcode;
}
