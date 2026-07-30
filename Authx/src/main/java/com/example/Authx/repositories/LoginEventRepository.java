package com.example.Authx.repositories;

import com.example.Authx.entity.LoginEvent;
import com.example.Authx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoginEventRepository extends JpaRepository<LoginEvent, UUID> {

    List<LoginEvent> findTop20ByUserOrderByCreatedAtDesc(User user);

    long countByUserAndStatusAndCreatedAtAfter(
            User user,
            LoginEvent.LoginStatus status,
            LocalDateTime after
    );

    List<LoginEvent> findTop10ByUserAndStatusOrderByCreatedAtDesc(User user, LoginEvent.LoginStatus loginStatus);
}
