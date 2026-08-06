package com.example.Authx.services;

import com.example.Authx.entity.AuditAction;
import com.example.Authx.entity.AuditLogs;
import com.example.Authx.entity.AuditStatus;
import com.example.Authx.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(UUID orgId, UUID userId, String email, AuditAction action,String resourceType, String resourceId,
                    String description, String ip, String agent){
        save(orgId,userId,email,action,resourceType,resourceId, AuditStatus.SUCCESS,description,ip,agent);
    }


    public void logFailure(UUID orgId, UUID userId, String email, AuditAction action,String resourceType, String resourceId,
                    String description, String ip, String agent){
        save(orgId,userId,email,action,resourceType,resourceId, AuditStatus.FAILURE,description,ip,agent);
    }

    private void save(UUID orgId, UUID userId, String email, AuditAction action,
                      String resourceType, String resourceId,
                      AuditStatus auditStatus, String description, String ip, String agent) {

    try{
        AuditLogs audit = AuditLogs.builder()
                .orgId(orgId)
                .userId(userId)
                .email(email)
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .status(auditStatus)
                .description(description)
                .ipAddress(ip)
                .userAgent(agent)
                .build();

        auditLogRepository.save(audit);
    }
    catch (Exception e){
        log.error("Audit Log save failed for action={}",action,e);
    }
    }


}
