package com.example.auth_app.service;

import com.example.auth_app.io.profileRequest;
import com.example.auth_app.io.profileResponse;

public interface profileService {
 profileResponse createProfile(profileRequest request);
}
