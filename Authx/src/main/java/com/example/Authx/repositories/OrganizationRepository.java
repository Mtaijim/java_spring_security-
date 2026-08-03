package com.example.Authx.repositories;

import com.example.Authx.entity.Organization;
import com.example.Authx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findBySlug(String slug);
    boolean existsBySlug(String slug);
    // get all orgs a user belongs to
    @Query("""
        SELECT o FROM Organization o
        JOIN OrgMembership m ON m.organization = o
        WHERE m.user = :user
        ORDER BY o.createdAt DESC
        """)
    List<Organization> findAllByMembers(User user );

//    get all org created by user
    List<Organization> findByCreatedBy(User user);
}
