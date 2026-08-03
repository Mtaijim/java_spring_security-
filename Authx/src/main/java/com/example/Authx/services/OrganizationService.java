package com.example.Authx.services;


import com.example.Authx.dtos.OrgDto;
import com.example.Authx.dtos.mfa.OrgMemberDto;
import com.example.Authx.entity.OrgMembership;
import com.example.Authx.entity.OrgRole;
import com.example.Authx.entity.Organization;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.OrganizationRepository;
import com.example.Authx.repositories.OrgMembershipRepository;
import com.example.Authx.repositories.userRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrgMembershipRepository orgMembershipRepository;
    private final OrganizationRepository organizationRepository;
    private final  userRepository userRepository;

    @Transactional
    public OrgDto createOrg(String name, String description, User creator ){
        String slug = generateSlug(name);
        if(organizationRepository.existsBySlug(slug)){
            slug = slug + "-"+ UUID.randomUUID().toString().substring(0,4);
        }
//        create organisation

        Organization org = Organization.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .createdBy(creator)
                .build();
        organizationRepository.save(org);

//        creator
        OrgMembership membership = OrgMembership.builder()
                .organization(org)
                .user(creator)
                .role(OrgRole.OWNER)
                .invitedBy(creator).build();
        orgMembershipRepository.save(membership);
        return toDto(org,OrgRole.OWNER,1L);
    }
// get all orgs for curr User

    public List<OrgDto> getMyOrgs(User user){
        return orgMembershipRepository.findByUser(user)
                .stream().map(m->
                        toDto(
                                m.getOrganization(),
                                m.getRole(),
                                orgMembershipRepository.countByOrganization(
                                        m.getOrganization()
                                )
                        )).collect(Collectors.toList());
    }

//    invite members to org
@Transactional
public void inviteMember(UUID orgId, String email,OrgRole role,User invitedBy)
{
    Organization org= organizationRepository.findById(orgId)
            .orElseThrow(()-> new RuntimeException("organization not found "));

    OrgMembership inviterMembership = orgMembershipRepository
            .findByOrganizationAndUser(org,invitedBy)
            .orElseThrow(()-> new RuntimeException(" Not a Member"));

    if(inviterMembership.getRole() == OrgRole.MEMBER ||
    inviterMembership.getRole() == OrgRole.VIEWER){
        throw new RuntimeException(
                "only OWNER and ADMIN can invite members"
        );
    }
//    find user to invite
    User invitedUser = userRepository.findByEmail(email).orElseThrow(()->
    new RuntimeException("User not found "+ email));

//    check not already member
    if(orgMembershipRepository.existsByOrganizationAndUser(org,invitedUser)){
        throw new RuntimeException("User is already a member ");
    }

//    can assign higher role
    if(role == OrgRole.OWNER){
        throw new RuntimeException("" +
                "cannot assign Owner role");

    }

    OrgMembership membership = OrgMembership.builder()
            .organization(org)
            .user(invitedUser)
            .role(role)
            .invitedBy(invitedBy)
            .build();
    orgMembershipRepository.save(membership);

}
// get all members of org
    public List<OrgMemberDto> getMembers(UUID orgId,User user){
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(()->new RuntimeException("Organization "));

//        must be member atleast
        if(!orgMembershipRepository.existsByOrganizationAndUser(org,user)){
            throw new RuntimeException("Must be Member at least ");
        }
        return orgMembershipRepository.findByOrganization(org)
                .stream().map(
                        m->
                                OrgMemberDto.builder()
                                        .userId(m.getUser().getId())
                                        .name(m.getUser().getName())
                                        .email(m.getUser().getEmail())
                                        .image(m.getUser().getImage())
                                        .role(m.getRole())
                                        .joinedAt(m.getJoinedAt())
                                        .build()
                ).collect(Collectors.toList());
    }
// change member role
    @Transactional
    public void changeMemberRole(UUID orgId, UUID targetId, OrgRole newRole, User requestBy){

      Organization org = organizationRepository.findById(orgId)
              .orElseThrow(()-> new RuntimeException("Organization not found"));
//   requester must be owner / admin
        OrgMembership requester = orgMembershipRepository.findByOrganizationAndUser(org,requestBy)
                .orElseThrow(()-> new RuntimeException("Not a member"));


        if (requester.getRole() != OrgRole.OWNER &&
                requester.getRole() != OrgRole.ADMIN) {
            throw new RuntimeException(
                    "Only OWNER or ADMIN can change roles"
            );
        }
// can't change OWNER role
        if (newRole == OrgRole.OWNER) {
            throw new RuntimeException(
                    "Cannot assign OWNER role"
            );
        }
User targetUser = userRepository.findById(targetId)
        .orElseThrow(()-> new RuntimeException(" user not Found"));

        OrgMembership target = orgMembershipRepository.findByOrganizationAndUser(org,targetUser)
                .orElseThrow(()->new RuntimeException(" user is not a member "));

        // can't change OWNER's role
        if (target.getRole() == OrgRole.OWNER) {
            throw new RuntimeException(
                    "Cannot change OWNER's role"
            );
        }
        target.setRole(newRole);
        orgMembershipRepository.save(target);


    }
    //        remove member from org



@Transactional
 public void removeMember(UUID orgId, UUID targetId, User requestedBy){
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(()->
                        new RuntimeException(" Organization not found "));

        OrgMembership requested = orgMembershipRepository.findByOrganizationAndUser(org,requestedBy)
                .orElseThrow(()-> new RuntimeException(" not a member "));

//        only ownwr or admin can remove members
     if (requested.getRole() != OrgRole.OWNER &&
             requested.getRole() != OrgRole.ADMIN) {
         throw new RuntimeException(
                 "Only OWNER or ADMIN can remove members"
         );
     }

     User targetUser = userRepository.findById(targetId)
             .orElseThrow(()-> new RuntimeException(" user not found "));

     OrgMembership targetMembership  =orgMembershipRepository.findByOrganizationAndUser(org,targetUser)
             .orElseThrow(()-> new RuntimeException("User is not a member "));

     if(targetMembership.getRole() == OrgRole.OWNER){
         throw new RuntimeException("Cannot remove Owner");
     }
     orgMembershipRepository.save(targetMembership);
 }
    //        delete org

    public void deleteOrg(UUID orgId , User requestedBy){
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(()->new RuntimeException("organization not found "));

        OrgMembership membership = orgMembershipRepository.findByOrganizationAndUser(
                org,requestedBy
        ).orElseThrow(()-> new RuntimeException(" not a member "));

        if(membership.getRole() != OrgRole.OWNER){
            throw new RuntimeException("Only owner can Delete Organization");
        }
//delete all member of org
        orgMembershipRepository.findByOrganization(org)
                .forEach(orgMembershipRepository::delete);

//        delete org
        organizationRepository.delete(org);

    }

    private OrgDto toDto( Organization org, OrgRole orgRole, long membercount) {
        return OrgDto.builder()
                .id(org.getId())
                .name(org.getName())
                .slug(org.getSlug())
                .description(org.getDescription())
                .createdAt(org.getCreatedAt())
                .memberCount(membercount)
                .myRole(orgRole)
                .build();
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }


}
