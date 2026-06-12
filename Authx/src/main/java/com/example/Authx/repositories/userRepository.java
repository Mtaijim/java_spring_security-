package com.example.Authx.repositories;

import com.example.Authx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface userRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);
}
