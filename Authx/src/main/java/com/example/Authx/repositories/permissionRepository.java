package com.example.Authx.repositories;

import com.example.Authx.entity.AppPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface permissionRepository extends JpaRepository<AppPermission,Long> {


    Optional<AppPermission> findByName(String name);

    List<AppPermission> findByCategory(String category);

    boolean existsByName(String name);

}
