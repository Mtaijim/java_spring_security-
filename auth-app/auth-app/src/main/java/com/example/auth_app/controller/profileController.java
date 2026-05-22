package com.example.auth_app.controller;

import com.example.auth_app.io.profileRequest;
import com.example.auth_app.io.profileResponse;
import com.example.auth_app.service.profileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class profileController {
    private final profileService profileService;

    public profileController(profileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public profileResponse register(@Valid @RequestBody profileRequest request){
profileResponse response = profileService.createProfile(request);
        return response;
    }
}
