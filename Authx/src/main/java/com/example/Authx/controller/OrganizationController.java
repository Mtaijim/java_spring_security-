package com.example.Authx.controller;

import com.example.Authx.dtos.OrgDto;
import com.example.Authx.dtos.mfa.OrgMemberDto;
import com.example.Authx.entity.OrgRole;
import com.example.Authx.entity.User;
import com.example.Authx.services.OrganizationService;
import lombok.RequiredArgsConstructor;
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
            @AuthenticationPrincipal User user
            ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizationService.createOrg(
                        body.get("name"),
                        body.get("description"),
                        user
                ));
    }

//    get all org of mine

    @GetMapping("/mine")
    public ResponseEntity<List<OrgDto>> getMyOrgs(
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(organizationService.getMyOrgs(user));
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
            @AuthenticationPrincipal User user
    ){
        organizationService.inviteMember(
                orgId,
                body.get("email"),
                OrgRole.valueOf(body.get("role").toUpperCase()),
                user
        );

        return ResponseEntity.ok("Member invited Successfully ");
    }

//    change role
    @PutMapping("/{orgId}/members/{userId}")
    public ResponseEntity<String> changeRole(
            @PathVariable UUID orgId,
            @PathVariable UUID userId,
            @RequestBody Map<String,String> body,
            @AuthenticationPrincipal User user
    ){
        organizationService.changeMemberRole(
                orgId,
                userId,
                OrgRole.valueOf(body.get("role").toUpperCase()),
                user
        );
        return ResponseEntity.ok("Role Updated Successfully");
    }

//    delete remove
    @DeleteMapping("/{orgId}/members/{userId}")
     public ResponseEntity<String> remove(
        @PathVariable UUID orgId,
        @PathVariable UUID userId,
        @AuthenticationPrincipal User user
){
        organizationService.removeMember(
                orgId,
                userId,
                user
        );
    return ResponseEntity.ok("Member removed successfully");
}

// delete org
//@DeleteMapping("/{orgId}")
//public ResponseEntity<String> deleteOrg(
//        @PathVariable UUID orgId,
//        @AuthenticationPrincipal User user) {
//
//    orgService.deleteOrg(orgId, user);
//
//    return ResponseEntity.ok("Organization deleted successfully");
//}
// best rest  practice
    @DeleteMapping("/{orgId}")
    public ResponseEntity<Void> deleteOrg(
            @PathVariable UUID orgId,
            @AuthenticationPrincipal User user) {

        organizationService.deleteOrg(orgId, user);
        return ResponseEntity.noContent().build();
    }
}
