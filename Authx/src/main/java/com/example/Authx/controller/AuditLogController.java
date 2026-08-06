package com.example.Authx.controller;

import com.example.Authx.dtos.AuditLogResponse;
import com.example.Authx.entity.AuditLogs;
import com.example.Authx.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final ModelMapper modelMapper;

    @GetMapping
    public Page<AuditLogResponse> getLogs(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable){
        return auditLogRepository.findAll(pageable).map(this::toDto);

    }

    private AuditLogResponse toDto(AuditLogs auditLogs) {

   return modelMapper.map(auditLogs,AuditLogResponse.class) ;
    }


}
