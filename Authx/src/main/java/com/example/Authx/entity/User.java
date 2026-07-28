package com.example.Authx.entity;

import com.example.Authx.services.UserService;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;
    @Column(unique = true, nullable = false)
    private String email;
    private String name;
    private String password;
    private String image;

    @Builder.Default
    private Boolean enable = true;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Provider provider = Provider.LOCAL;
    private String providerId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled = false;

    @Column(name = "totp_secret")
    private String totpSecret;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null)
            createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // changed .name for rbac
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role
                        .getName().name()))
                .toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }



    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enable;
    }


// rate limiting fields

    @Column(name = "failed_attempts",nullable = false)
    @Builder.Default
    private Integer failedAttempts = 0;


    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

 public boolean isAccountLocked(){
     return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
 }
    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked();
    }
 public void incrementFailedAttempts(){
     this.failedAttempts =
             (this.failedAttempts == null ?
                     0 : this.failedAttempts) + 1;
 }

 public void resetLockout(){
     this.failedAttempts = 0 ;
     this.lockedUntil = null;
 }
 public void lockFor(int minutes){
     this.lockedUntil = LocalDateTime.now().plusMinutes(minutes);
 }


}
