package com.example.Authx.repositories;

import com.example.Authx.entity.AuditAction;
import com.example.Authx.entity.AuditLogs;
import com.example.Authx.entity.AuditStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLogs, UUID> {

    Page<AuditLogs> findByUserId(UUID userId, Pageable pageable);
    Page<AuditLogs> findByAction(AuditAction action , Pageable pageable);
    Page<AuditLogs> findByStatus(AuditStatus status, Pageable pageable);
    Page<AuditLogs> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to
    ,Pageable pageable);


    Page<AuditLogs> findByOrgId(UUID orgId, Pageable pageable);
    Page<AuditLogs> findByOrgIdAndAction(UUID orgId, AuditAction action, Pageable pageable);


}
