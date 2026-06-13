package com.example.Authx.services.Impl;

import com.example.Authx.dtos.UserDto;
import com.example.Authx.services.AuthService;
import com.example.Authx.services.UserService;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private  final PasswordEncoder passwordEncoder;
    @Override
    public UserDto registerUser(UserDto userDto) {
//        verify email etc logic ....
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
         return userService.createUser(userDto);

    }
}
