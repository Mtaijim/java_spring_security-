package com.example.auth_app.repository;

import com.example.auth_app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface userRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByEmail(String email);
    Boolean existsByEmail(String email);
}
