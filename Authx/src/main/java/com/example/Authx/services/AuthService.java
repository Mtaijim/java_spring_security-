package com.example.Authx.services;

import com.example.Authx.dtos.UserDto;

public interface AuthService {
    UserDto registerUser(UserDto userDto);

    void verifyEmail(String token);
    void forgotPassword(String email);
    void resetPassword(String token , String newPassword);

}
