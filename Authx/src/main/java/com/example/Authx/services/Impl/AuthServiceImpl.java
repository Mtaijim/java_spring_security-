package com.example.Authx.services.Impl;

import com.example.Authx.dtos.RoleDto;
import com.example.Authx.dtos.UserDto;
import com.example.Authx.services.AuthService;
import com.example.Authx.services.UserService;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Authx.entity.Role;
import com.example.Authx.entity.RoleType;
import com.example.Authx.repositories.RoleRepository;

import java.util.Set;
import java.util.UUID;


@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private  final PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(UserDto userDto) {
//        verify email etc logic ....
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

//        assign default role

         return userService.createUser(userDto);

    }
}
