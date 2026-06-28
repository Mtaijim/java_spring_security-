package com.example.Authx.services;

import com.example.Authx.dtos.UserDto;
import com.example.Authx.entity.RoleType;
import com.example.Authx.entity.User;


public interface UserService {

 UserDto createUser(UserDto userDto);
 UserDto getUserByEmail(String email);
 UserDto updateUser(UserDto userDto, String userId);

 void deleteUser(String userId);

 UserDto getUserById(String userId);
 Iterable<UserDto> getAllUsers();
 UserDto assignRole(String userId, RoleType role);

 User getRawUserById(String userId);
}
