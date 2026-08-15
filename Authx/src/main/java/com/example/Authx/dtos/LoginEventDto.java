package com.example.Authx.dtos;

import com.example.Authx.entity.LoginEvent.LoginStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginEventDto {
    private UUID id;
    private String ipAddress;
    private String device;
    private String os;
    private LoginStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
}