package com.example.Authx.repositories;

import com.example.Authx.entity.PasswordHistory;
import com.example.Authx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, UUID> {

List<PasswordHistory> findTop5ByUserOrderByCreatedAtDesc(User user);
}
