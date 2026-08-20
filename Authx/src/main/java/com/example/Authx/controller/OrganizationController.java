package com.example.Authx.controller;

import com.example.Authx.dtos.OrgDto;
import com.example.Authx.dtos.PendingInviteDto;
import com.example.Authx.dtos.mfa.OrgMemberDto;
import com.example.Authx.entity.OrgRole;
import com.example.Authx.entity.User;
import com.example.Authx.services.OrganizationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.example.Authx.helper.RequestHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orgs")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrgDto> create(
            @RequestBody Map<String ,String> body,
            @AuthenticationPrincipal User user,
            HttpServletRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizationService.createOrg(
                        body.get("name"),
                        body.get("description"),
                        user,
                      (RequestHelper.getClientIp(request)),
                        request.getHeader("User-Agent")
                ));
    }

//    get all org of mine

    @GetMapping("/mine")
    public ResponseEntity<List<OrgDto>> getMyOrgs(
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(organizationService.getMyOrgs(user));
    }


    // get single org details
    @GetMapping("/{orgId}")
    public ResponseEntity<OrgDto> getOrg(
            @PathVariable UUID orgId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(organizationService.getOrgById(orgId, user));
    }
//list of all members
    @GetMapping("/{orgId}/members")
    public ResponseEntity<List<OrgMemberDto>> getMembers(
            @PathVariable UUID orgId,
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(organizationService.getMembers(orgId,user));
    }

//    invite member
    @PostMapping("/{orgId}/invite")
    public ResponseEntity<String> invite(
            @PathVariable UUID orgId,
            @RequestBody Map<String,String> body,
            @AuthenticationPrincipal User user,HttpServletRequest request
    ){
        organizationService.inviteMember(
                orgId,
                body.get("email"),
                OrgRole.valueOf(body.get("role").toUpperCase()),
                user,
                (RequestHelper.getClientIp(request)),
                request.getHeader("User-Agent")
        );

        return ResponseEntity.ok("Member invited Successfully ");
    }
// accept invite

    @PostMapping("/{orgId}/invites/{membershipId}/accept")
    public ResponseEntity<String> acceptInvite(
            @PathVariable UUID orgId,
            @PathVariable UUID membershipId,
            @AuthenticationPrincipal User user,
            HttpServletRequest request
    ){
        organizationService.acceptInvite(
                membershipId,
                user,
                RequestHelper.getClientIp(request),
                request.getHeader("User-Agent")
        );
        return ResponseEntity.ok("Invite accepted ");
    }

//decline Invite

    @PostMapping("/{orgId}/invites/{membershipId}/decline")
    public ResponseEntity<String> declineInvite(
            @PathVariable UUID orgId,
            @PathVariable UUID membershipId,
            @AuthenticationPrincipal User user,
            HttpServletRequest request
    ){
        organizationService.declineInvite(
                membershipId,
                user,
                RequestHelper.getClientIp(request),
                request.getHeader("User-Agent")
        );
        return ResponseEntity.ok("Invite declined");
    }
    @GetMapping("/invites/pending")
    public ResponseEntity<List<PendingInviteDto>> getMyPendingInvites(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(organizationService.getMyPendingInvites(user));
    }

//    change role
    @PutMapping("/{orgId}/members/{userId}")
    public ResponseEntity<String> changeRole(
            @PathVariable UUID orgId,
            @PathVariable UUID userId,
            @RequestBody Map<String,String> body,
            @AuthenticationPrincipal User user,HttpServletRequest request
    ){
        organizationService.changeMemberRole(
                orgId,
                userId,
                OrgRole.valueOf(body.get("role").toUpperCase()),
                user,
                (RequestHelper.getClientIp(request)),
                request.getHeader("User-Agent")
        );
        return ResponseEntity.ok("Role Updated Successfully");
    }

//    delete remove
    @DeleteMapping("/{orgId}/members/{userId}")
     public ResponseEntity<String> remove(
        @PathVariable UUID orgId,
        @PathVariable UUID userId,
        @AuthenticationPrincipal User user,HttpServletRequest request
){
        organizationService.removeMember(
                orgId,
                userId,
                user,
                (RequestHelper.getClientIp(request)),
                request.getHeader("User-Agent")
        );
    return ResponseEntity.ok("Member removed successfully");
}


// Delete Orgs
    @DeleteMapping("/{orgId}")
    public ResponseEntity<Void> deleteOrg(
            @PathVariable UUID orgId,
            @AuthenticationPrincipal User user
    ,HttpServletRequest request) {

        organizationService.deleteOrg(orgId, user
        , (RequestHelper.getClientIp(request)),
              request.getHeader("User-Agent")  );
        return ResponseEntity.noContent().build();
    }


}
