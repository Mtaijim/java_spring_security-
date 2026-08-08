package com.example.Authx.repositories;

import com.example.Authx.entity.RiskLevel;
import com.example.Authx.entity.RiskScore;
import com.example.Authx.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RiskScoreRepository extends JpaRepository<RiskScore, UUID> {

    List<RiskScore> findByUserId(UUID userId);
    Page<RiskScore> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<RiskScore> findByLevelOrderByCreatedAtDesc(RiskLevel level , Pageable pageable);

}
