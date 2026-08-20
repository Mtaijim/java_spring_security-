package com.example.Authx.controller;

import com.example.Authx.dtos.RiskScoreResponse;
import com.example.Authx.entity.RiskLevel;
import com.example.Authx.entity.RiskScore;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.RiskScoreRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RiskScoreController {

    private final RiskScoreRepository riskScoreRepository;
    private final ModelMapper modelMapper;


    @GetMapping("/api/v1/risk/me")
    public Page<RiskScoreResponse> getMyRiskHistory(
            @AuthenticationPrincipal User user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
  return  riskScoreRepository
          .findByUserIdOrderByCreatedAtDesc(user.getId(),pageable).map(this::todto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/v1/admin/risk")
    public Page<RiskScoreResponse> getHighRiskEvents(
            @RequestParam(required = false) RiskLevel level,
            @PageableDefault(sort = "createdAt" , direction = Sort.Direction.DESC)
            Pageable pageable
            ){
        if (level != null) {
            return riskScoreRepository
                    .findByLevelOrderByCreatedAtDesc(level, pageable)
                    .map(this::todto);
        }
        return riskScoreRepository.findAll(pageable)
                .map(this::todto);
    }

    private RiskScoreResponse todto(RiskScore riskScore) {
        return modelMapper.map(riskScore, RiskScoreResponse.class);
    }
}
