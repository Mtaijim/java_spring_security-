package com.example.Authx.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_user_id",columnList = "user_id"),
                @Index(name = "idx_audit_org_id", columnList = "org_id"),
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID orgId;

    private UUID userId;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "action",columnDefinition = "VARCHAR(64)")
    private AuditAction action;

    private String resourceType;

    private String resourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditStatus status;

    @Column(length = 1000)
    private String description;

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
