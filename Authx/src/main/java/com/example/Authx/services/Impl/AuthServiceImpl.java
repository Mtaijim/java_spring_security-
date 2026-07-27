package com.example.Authx.services.Impl;

import com.example.Authx.dtos.AuthResponse;
import com.example.Authx.dtos.LoginRequest;
import com.example.Authx.dtos.RoleDto;
import com.example.Authx.dtos.UserDto;
import com.example.Authx.entity.*;
import com.example.Authx.helper.DeviceParser;
import com.example.Authx.repositories.EmailVerificationTokenRepository;
import com.example.Authx.repositories.PasswordResetTokenRepository;
import com.example.Authx.repositories.userRepository;
import com.example.Authx.security.JwtService;
import com.example.Authx.services.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final Emailvalidator emailvalidator;
    private final JwtService jwtServices;
//    login services
    private final LoginEventServices loginEventServices;
    private final DeviceParser deviceParser;

    @Override
    public UserDto registerUser(UserDto userDto) {
        emailvalidator.Validate(userDto.getEmail());
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

    @Override
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            passwordResetTokenRepository.deleteByUser(user);
            passwordResetTokenRepository.flush();


        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        passwordResetTokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(email,token);
    });
    }

    @Override
    public void resetPassword(String token, String newPassword) {
     PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
             .orElseThrow(()-> new IllegalArgumentException("invalid reset token"));


     if(resetToken.isUsed()){
         throw new IllegalArgumentException("TOKEN IS ALREADY IN USED ");
     }
     if(resetToken.getExpiresAt().isBefore(Instant.now())){
         throw new IllegalArgumentException("TOKEN IS EXPIRED ");
     }

     User user = resetToken.getUser();
     user.setPassword(passwordEncoder.encode(newPassword));
     userRepository.save(user);

     resetToken.setUsed(true);
     passwordResetTokenRepository.save(resetToken);
    }
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest){
        User user = userRepository.findByEmail(request.email()).orElseThrow(()->new RuntimeException("Invalid Email or Password "));

    if(!user.isEnabled()){
        throw new RuntimeException("please verify your email first .");
    }

    if(!passwordEncoder.matches(request.password(),user.getPassword())){
      loginEventServices.recordFailure(user,httpRequest,"invalid password");
        throw new RuntimeException("Invalid email or password");
    }
loginEventServices.recordSucess(user, httpRequest);

        String token = jwtServices.generateAccessToken(user);
        UserDto userDto = userService.getUserById(user.getId().toString());
        return AuthResponse.builder()
                .accessToken(token)
                .user(userDto)
                .build();
    }
}
