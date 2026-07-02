package com.example.Authx.dtos;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType,
        UserDto user,
        boolean mfaRequired,
        String mfaToken
) {
 public static TokenResponse of(String accessToken, String refreshToken,
                                long expiresIn , UserDto user){
     return  new TokenResponse(accessToken,refreshToken,expiresIn,"Bearer",user,false,null);
 }

 // mfa
    public static TokenResponse mfaRequired(String mfaToken){
     return new TokenResponse(null,null,0,"Bearer",null,true,mfaToken);
    }
}
