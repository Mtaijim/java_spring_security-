package com.example.Authx.services;

import com.example.Authx.entity.*;
import com.example.Authx.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiskScoringService {
    private final AuditLogRepository auditLogRepository;


    public RiskScore calculateRisk(User user, String ip , String userAgent){
        int score  = 0 ;
        StringBuilder reason = new StringBuilder();

        List<AuditLogs> pastLogin = auditLogRepository.
                findByUserId(user.getId(), Pageable.unpaged()).getContent();

//        known device
        boolean KnownDevice = pastLogin.stream()
                .anyMatch(auditLogs -> userAgent != null & userAgent.equals(auditLogs.getUserAgent()));
        if(!KnownDevice && !pastLogin.isEmpty()){
            score += 30;
            reason.append("New Device .");
        }

//        known Ip
        boolean KnownIp = pastLogin.stream()
                .anyMatch(auditLogs -> ip != null & ip.equals(auditLogs.getIpAddress()));
        if (!KnownIp && !pastLogin.isEmpty()){
            score +=25;
            reason.append("new Ip/location");

        }

//        check failed attempt
        LocalDateTime fifteenMinAgo = LocalDateTime.now().minusMinutes(15);
        long recentFailures  = pastLogin.stream()
                .filter(auditLogs -> auditLogs.getAction() == AuditAction.LOGIN_FAILED)
                .filter(auditLogs -> auditLogs.getCreatedAt().isAfter(fifteenMinAgo))
                .count();
        if (recentFailures>3){
            score +=20;
            reason.append(recentFailures).append("failed attempts recently. ");
        }
//        odd time login
        LocalTime now = LocalTime.now();
        if(now.isAfter(LocalTime.MIDNIGHT) && now.isBefore(LocalTime.of(5,0))){
            score +=15;
            reason.append("unusual login time .");
        }
        score = Math.min(score,100);

        RiskLevel level = score <= 30 ? RiskLevel.LOW
                :score<=60 ? RiskLevel.MEDIUM
                :RiskLevel.HIGH;

        return RiskScore.builder()
                .id(user.getId())
                .email(user.getEmail())
                .score(score)
                .level(level)
                .reasons(!reason.isEmpty() ? reason.toString() : "Normal login pattern")
                .ipAddress(ip)
                .userAgent(userAgent)
                .build();
    }

}
