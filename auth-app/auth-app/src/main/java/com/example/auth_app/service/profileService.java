package com.example.auth_app.service;

import com.example.auth_app.io.profileRequest;
import com.example.auth_app.io.profileResponse;

public interface profileService {
 profileResponse createProfile(profileRequest request);
 profileResponse getProfile(String  email);
 void sendResetOtp(String email);
 void resetPassword (String email , String otp , String newPassword);
 void  sendOtp(String email);
 void verifyOtp(String email, String otp);

}
