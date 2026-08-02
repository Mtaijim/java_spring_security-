package com.example.Authx.entity;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(unique = true, nullable = false)
    private String name ;

    private String description;

    private String category ;
}
