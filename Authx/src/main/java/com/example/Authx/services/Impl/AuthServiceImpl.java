package com.example.Authx.services.Impl;

import com.example.Authx.dtos.RoleDto;
import com.example.Authx.dtos.UserDto;
import com.example.Authx.entity.EmailVerificationToken;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.EmailVerificationTokenRepository;
import com.example.Authx.repositories.userRepository;
import com.example.Authx.services.AuthService;
import com.example.Authx.services.EmailService;
import com.example.Authx.services.UserService;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Authx.entity.Role;
import com.example.Authx.entity.RoleType;
import com.example.Authx.repositories.RoleRepository;

import java.time.Instant;
import java.util.UUID;


@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private  final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final  userRepository userRepository;

    @Override
    public UserDto registerUser(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        assign default role
        userDto.setEnable(false);
        UserDto savedUser =  userService.createUser(userDto);
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(userService.getRawUserById(savedUser.getId().toString()))
                .expiresAt(Instant.now().plusSeconds(86400)).build();
        tokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(savedUser.getEmail(), token);
        return savedUser;
    }

    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken emailVerificationToken = tokenRepository.findByToken(token)
                .orElseThrow(()-> new IllegalArgumentException("Invalid verification token") );

         if(emailVerificationToken.isUsed()){
             throw new IllegalArgumentException("token is already used");
         }

         if(emailVerificationToken.getExpiresAt().isBefore(Instant.now())){
             throw new IllegalArgumentException("Token is Expired ");

         }

         User user = emailVerificationToken.getUser();
         user.setEnable(true);
         userRepository.save(user);

         emailVerificationToken.setUsed(true);
         tokenRepository.save(emailVerificationToken);

    }
}
