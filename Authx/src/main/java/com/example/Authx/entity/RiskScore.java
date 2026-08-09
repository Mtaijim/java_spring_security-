package com.example.Authx.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "risk_scores", indexes = {
        @Index(name = "idx_risk_user_id", columnList = "userId"),
        @Index(name = "idx_risk_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskScore {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;
    private String email ;
    private int score;

    @Enumerated(EnumType.STRING)
    private RiskLevel level;

    @Column(length = 500)
    private String reasons;

    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }


}
