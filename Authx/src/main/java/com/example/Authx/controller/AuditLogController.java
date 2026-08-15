package com.example.Authx.controller;

import com.example.Authx.dtos.AuditLogResponse;
import com.example.Authx.entity.AuditAction;
import com.example.Authx.entity.AuditLogs;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.AuditLogRepository;
import com.example.Authx.services.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService; // ← ADD
    private final ModelMapper modelMapper;

    @GetMapping
    public Page<AuditLogResponse> getLogs(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable){
        return auditLogRepository.findAll(pageable).map(this::toDto);

    }

    @GetMapping("/me")
    public Page<AuditLogResponse>  getMyLogs(
            @AuthenticationPrincipal User user,
            @PageableDefault(sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        return auditLogRepository
                .findByUserId(user.getId(),pageable)
                .map(this::toDto);
    }

    @GetMapping("/action/{action}")
    public Page<AuditLogResponse> getByAction(
            @PathVariable AuditAction action,
            @PageableDefault(sort = "createdAt",
                    direction = Sort.Direction.DESC)
            Pageable pageable) {

        return auditLogRepository
                .findByAction(action, pageable)
                .map(this::toDto);
    }



    private AuditLogResponse toDto(AuditLogs auditLogs) {

   return modelMapper.map(auditLogs,AuditLogResponse.class) ;
    }


}
