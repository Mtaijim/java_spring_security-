package com.example.Authx.services;

import com.example.Authx.dtos.AuthResponse;
import com.example.Authx.dtos.LoginRequest;
import com.example.Authx.dtos.UserDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    UserDto registerUser(UserDto userDto);

    void verifyEmail(String token);
    void forgotPassword(String email);
    void resetPassword(String token , String newPassword);

    AuthResponse login(LoginRequest request,
                       HttpServletRequest httpRequest);
    AuthResponse verifyRiskOtp(String email, String otp);
}
