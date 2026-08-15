package com.example.Authx.controller;

import com.example.Authx.dtos.LoginEventDto;
import com.example.Authx.entity.LoginEvent;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.userRepository;
import com.example.Authx.services.LoginEventServices;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final LoginEventServices loginEventServices;
    private final userRepository userRepository;

    @GetMapping("/history")
    public ResponseEntity<List<LoginEventDto>> getHistory(@AuthenticationPrincipal User user){

        if (user == null) {
            String email = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal()
                    .toString();
            user = userRepository.findByEmail(email).orElseThrow();
        }

        List<LoginEventDto> history = loginEventServices
                .getHistory(user)
                .stream()
                .map(e -> LoginEventDto.builder()
                        .id(e.getId())
                        .ipAddress(e.getIpAddress())
                        .device(e.getDevice())
                        .os(e.getOs())
                        .status(e.getStatus())
                        .failureReason(e.getFailureReason())
                        .createdAt(e.getCreatedAt())
                        .build()
                )
                .toList();

        return ResponseEntity.ok(history);
    }
}
