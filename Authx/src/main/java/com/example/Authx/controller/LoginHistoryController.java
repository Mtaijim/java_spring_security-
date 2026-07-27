package com.example.Authx.controller;

import com.example.Authx.entity.LoginEvent;
import com.example.Authx.entity.User;
import com.example.Authx.services.LoginEventServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth/v1/auth")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final LoginEventServices loginEventServices;

    @GetMapping("/history")
    public ResponseEntity<List<LoginEvent>> getHistory(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(loginEventServices.getHistory(user));
    }
}
