package com.example.Authx.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "org_memberships",
//      one user can only have one role per org
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"org_id", "user_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // which org
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id" ,nullable = false)
    private Organization organization;


//    which User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//    separate from global ROLE_ADMIN/ROLE_USER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrgRole role;


    @Column(name = "joined_at")
    private Instant joinedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private User invitedBy;


    @PrePersist
    public void prePersist() {
        joinedAt = Instant.now();
    }

}
