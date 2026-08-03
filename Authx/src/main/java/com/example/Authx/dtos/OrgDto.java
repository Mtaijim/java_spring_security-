package com.example.Authx.dtos;

import com.example.Authx.entity.OrgRole;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgDto {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private Instant createdAt;
    private long memberCount;
    private OrgRole myRole;  // current user's role in this org
}