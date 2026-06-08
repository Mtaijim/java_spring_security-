package com.example.AuthNext.dtos;

import com.example.AuthNext.entity.Provider;
import com.example.AuthNext.entity.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class userDto {


    private UUID id;
    private String email;
    private String name;
    private String password;
    private String image;
    private boolean enable= true;
    private Instant createdAt =Instant.now();
    private Instant updatedAt = Instant.now();
    private Provider provider =Provider.LOCAL;
    private Set<RoleDto> roles = new HashSet<>();


}
