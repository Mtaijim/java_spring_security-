package com.example.Authx.dtos.mfa;

import com.example.Authx.entity.OrgRole;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgMemberDto {
    private UUID userId;
    private String name;
    private String email;
    private String image;
    private OrgRole role;
    private Instant joinedAt;
}
