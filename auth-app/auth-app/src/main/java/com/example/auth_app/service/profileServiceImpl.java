package com.example.auth_app.service;

import com.example.auth_app.entity.UserEntity;
import com.example.auth_app.io.profileRequest;
import com.example.auth_app.io.profileResponse;
import com.example.auth_app.repository.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
public class profileServiceImpl implements profileService {
    private final userRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public profileResponse createProfile(profileRequest request) {
        UserEntity newProfile = convertToUserEntity(request);
        if(!userRepository.existsByEmail(request.getEmail())){
            userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exist ");


    }

    @Override
    public profileResponse getProfile(String email) {

      UserEntity existingUser  =   userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(" not found "+email));
          return convertToProfileResponse(existingUser);

    }

    @Override
    public void sendResetOtp(String email) {
     UserEntity existingEntity =userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("user not found "+ email));

   String otp= String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

         long expiryTime = System.currentTimeMillis()+(15*60*1000);
   existingEntity.setResetOtp(otp);
   existingEntity.setResetOtpExpiredAt(expiryTime);

   userRepository.save(existingEntity);
try{
emailService.SendResetOtpEmail(existingEntity.getEmail(),otp);
}catch (Exception e){
    throw new RuntimeException("unable to send Email");
}

    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
     UserEntity existingUser=    userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("user not found "+email));
               if (existingUser.getResetOtp()==null || !existingUser.getResetOtp().equals(otp)){
                   throw new RuntimeException("invalid otp");
               }
               if(existingUser.getResetOtpExpiredAt()<System.currentTimeMillis()){
                   throw new RuntimeException("otp Expired");
               }
               existingUser.setPassword(passwordEncoder.encode(newPassword));
               existingUser.setResetOtp(null);
               existingUser.setResetOtpExpiredAt(0L);
               userRepository.save(existingUser);
    }

    @Override
    public void sendOtp(String email) {
        UserEntity exisitingUser= userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("user not found"+email));
   if(exisitingUser.getIsAccountVerified()!=null && exisitingUser.getIsAccountVerified()){
       return;
   }
        String otp= String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        long expiryTime = System.currentTimeMillis()+(24*60*60*1000);
        exisitingUser.setVerifyOtp(otp);
        exisitingUser.setVerifyOtpExpiredAt(expiryTime);
        userRepository.save(exisitingUser);

        try{
 emailService.SendOtpEmail(exisitingUser.getEmail(),otp);
        }catch (Exception e){
            throw new RuntimeException("unable to send email");
        }

    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity exisitingUser= userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("user not found"+email));

         if(exisitingUser.getVerifyOtp()==null || !exisitingUser.getVerifyOtp().equals(otp)){
             throw new RuntimeException("invalid otp");
         }
         if (exisitingUser.getVerifyOtpExpiredAt()<System.currentTimeMillis()){
             throw new RuntimeException("otp is Expired");
         }
         exisitingUser.setIsAccountVerified(true);
         exisitingUser.setVerifyOtp(null);
         exisitingUser.setVerifyOtpExpiredAt(0L);
         userRepository.save(exisitingUser);
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
