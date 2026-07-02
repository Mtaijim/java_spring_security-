package com.example.Authx.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mfa_backup_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MfaBackUpcode {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "code_hash", nullable = false)
    private String codeHash ;

    @Column(nullable = false)
    private boolean used ;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
