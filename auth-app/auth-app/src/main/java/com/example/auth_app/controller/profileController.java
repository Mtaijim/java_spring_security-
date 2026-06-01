package com.example.auth_app.controller;

import com.example.auth_app.io.profileRequest;
import com.example.auth_app.io.profileResponse;
import com.example.auth_app.service.EmailService;
import com.example.auth_app.service.profileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
public abstract class profileController {
    private final profileService profileService;
    private final EmailService emailService;

    public profileController(profileService profileService, EmailService emailService) {
        this.profileService = profileService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public profileResponse register(@Valid @RequestBody profileRequest request){
profileResponse response = profileService.createProfile(request);
emailService.sendWelcomeEmail(response.getEmail(),response.getName());
        return response;
    }
@GetMapping("/profile")
    public profileResponse getProfile(@CurrentSecurityContext (expression = "authentication?.name")String email){

 return profileService.getProfile(email);
}

}
