package com.example.Authx.services.Impl;

import com.example.Authx.dtos.UserDto;
import com.example.Authx.services.AuthService;
import com.example.Authx.services.UserService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    @Override
    public UserDto registerUser(UserDto userDto) {
//        verify email etc logic ....
       UserDto userDto1 = userService.createUser(userDto);
        return  userDto1;
    }
}
