package com.example.Authx.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "login_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id")
    private User user ;

    @Column(name = "ipAddress")
    private String ipAddress;

    @Column(name = "devices")
    private String device;

    @Column(name = "os")
    private String os;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LoginStatus status;


    @Column(name = "failure_reason")
    private String failureReason;


    @Column(name = "createdAt")
    private LocalDateTime createdAt;


    public void prePersist(){
        createdAt= LocalDateTime.now();
    }

    public enum LoginStatus {
        SUCCESS,FAILED
    }

}
