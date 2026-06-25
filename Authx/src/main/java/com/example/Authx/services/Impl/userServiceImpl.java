package com.example.Authx.services.Impl;

import com.example.Authx.dtos.UserDto;
import com.example.Authx.entity.Provider;
import com.example.Authx.entity.User;
import com.example.Authx.exceptions.ResourceNotFoundException;
import com.example.Authx.helper.UserHelper;
import com.example.Authx.repositories.userRepository;
import com.example.Authx.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class userServiceImpl implements UserService {

    private final userRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        if(userDto.getEmail()==null || userDto.getEmail().isBlank()){
            throw new IllegalArgumentException("Email is Required ");
        }
        if(userRepository.existsByEmail(userDto.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }
           User user = modelMapper.map(userDto, User.class);
        user.setEnable(true);
            user.setProvider(userDto.getProvider() !=null ? userDto.getProvider(): Provider.LOCAL);
//           role assign here to user __ for authorization
//TODO:
            User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser,UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
     User user =   userRepository
                .findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException(" User not found with given email"));
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        UUID uid = UserHelper.parseUUID(userId);
        User existingUser= userRepository.findById(uid).orElseThrow(()-> new ResourceNotFoundException(" User not found with given id"));
        if(userDto.getName() != null) existingUser.setName(userDto.getName());
        if (userDto.getImage() !=null) existingUser.setImage(userDto.getImage());
        if(userDto.getProvider()!=null) existingUser.setProvider(userDto.getProvider());
//TODO :change password logic here .....
        if (userDto.getPassword()!=null) existingUser.setPassword(userDto.getPassword());
        existingUser.setEnable(userDto.getEnable());
        existingUser.setUpdatedAt(Instant.now());
   User updateUser = userRepository.save(existingUser);
        return modelMapper.map(updateUser,UserDto.class);
    }

    @Override
    public void deleteUser(String userId) {
       UUID uid = UserHelper.parseUUID(userId);
      User user = userRepository.findById(uid).orElseThrow(()-> new ResourceNotFoundException("user not found with given id "));
       userRepository.delete(user);
    }

    @Override
    public UserDto getUserById(String userId) {
      User user = userRepository.findById(UserHelper.parseUUID(userId)).orElseThrow(()->new ResourceNotFoundException("user not found with given id "));
      return modelMapper.map(user,UserDto.class);
    }

    @Override
    @Transactional
    public Iterable<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                        .map(user -> modelMapper.map(user,UserDto.class))
                .toList();
    }


}
