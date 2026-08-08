package com.example.Authx.dtos;

import com.example.Authx.entity.RiskLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RiskScoreResponse { private UUID id;
    private String email;
    private int score;
    private RiskLevel level;
    private String reasons;
    private String ipAddress;
    private LocalDateTime createdAt;

}
