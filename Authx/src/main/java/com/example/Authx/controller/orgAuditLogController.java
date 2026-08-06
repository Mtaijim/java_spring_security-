package com.example.Authx.controller;

import com.example.Authx.dtos.AuditLogResponse;
import com.example.Authx.entity.*;
import com.example.Authx.repositories.AuditLogRepository;
import com.example.Authx.repositories.OrgMembershipRepository;
import com.example.Authx.repositories.OrganizationRepository;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orgs/{orgId}/audit-logs")
@RequiredArgsConstructor
public class orgAuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final OrganizationRepository organizationRepository;
    private final OrgMembershipRepository orgMembershipRepository;
    private final ModelMapper modelMapper;

@GetMapping
public Page<AuditLogResponse> getOrgLogs(
        @PathVariable UUID orgId,
        @PageableDefault(sort = "createdAt",direction = Sort.Direction.DESC)
        Pageable pageable,
        @AuthenticationPrincipal User user
        ){

    Organization organization = organizationRepository.findById(orgId)
            .orElseThrow(()-> new RuntimeException("organization id not found"));



    OrgMembership membership = orgMembershipRepository
            .findByOrganizationAndUser(organization, user)
            .orElseThrow(() -> new RuntimeException("Not a member"));


    if (membership.getRole() != OrgRole.OWNER &&
            membership.getRole() != OrgRole.ADMIN) {
        throw new RuntimeException("Only OWNER or ADMIN can view audit logs");
    }
    return auditLogRepository.findByOrgId(orgId, pageable)
            .map(this::toDto);
}

    private AuditLogResponse toDto(AuditLogs auditLogs) {
        return modelMapper.map(auditLogs, AuditLogResponse.class);
    }
}
