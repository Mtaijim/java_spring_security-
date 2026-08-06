package com.example.Authx.dtos;

import com.example.Authx.entity.AuditAction;
import com.example.Authx.entity.AuditStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AuditLogResponse {
    private UUID id;
    private UUID orgId;
    private String email;
    private AuditAction action;
    private AuditStatus status;
    private String resourceType;
    private String resourceId;
    private String description;
    private String ipAddress;
    private LocalDateTime createdAt;
}
