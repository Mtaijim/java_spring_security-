package com.example.auth_app.service;

import com.example.auth_app.entity.UserEntity;
import com.example.auth_app.io.profileRequest;
import com.example.auth_app.io.profileResponse;
import com.example.auth_app.repository.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class profileServiceImpl implements profileService {
    private final userRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public profileResponse createProfile(profileRequest request) {
        UserEntity newProfile = convertToUserEntity(request);
        if(!userRepository.existsByEmail(request.getEmail())){
            userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exist ");


    }

    private profileResponse convertToProfileResponse(UserEntity newProfile) {

        return profileResponse.builder()
                .userId(newProfile.getUserId())
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(profileRequest request) {
     return  UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
             .verifyOtpExpiredAt(0L)
                .verifyOtp(null)
             .resetOtpExpiredAt(0L).resetOtp(null)
                .build();
    }
}
