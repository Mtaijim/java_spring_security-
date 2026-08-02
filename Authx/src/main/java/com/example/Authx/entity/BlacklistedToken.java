package com.example.Authx.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "blacklisted_tokens",
        indexes = @Index(
                name = "idx_jti",
                columnList = "jti",
                unique = true
        ))
// index on jti = fast lookup on every request
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jti;

//    logout time
@Column(name = "revoked_at", nullable = false)
private Instant revokedAt;

//expired time

    @Column(name = "expires_at",nullable = false)
    private Instant expiresAt;

    @PrePersist
    public void prepersist(){
        if(revokedAt == null){
            revokedAt = Instant.now();
        }
    }

}
