package com.example.Authx.dtos;

import com.example.Authx.entity.OrgRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingInviteDto {

    private UUID  OrgMembershipId;
    private UUID orgId;
    private String orgName;
    private String orgSlug;
    private OrgRole role;
    private String inviteByName;
    private String invitedByEmail;
    private Instant invitedAt;

}
