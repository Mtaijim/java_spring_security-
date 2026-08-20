package com.example.Authx.repositories;

import com.example.Authx.entity.InvitedStatus;
import com.example.Authx.entity.OrgMembership;
import com.example.Authx.entity.Organization;
import com.example.Authx.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrgMembershipRepository extends JpaRepository<OrgMembership, UUID> {

   //    get all members of an org
    List<OrgMembership> findByOrganization(
            Organization organization
    );
  // get all members of an orgs with a given status
   List<OrgMembership> findByOrganizationAndStatus(Organization organization, InvitedStatus status);

    //       get specific membership
    Optional<OrgMembership> findByOrganizationAndUser(
            Organization organization, User user
    );
   // get specific membership with a given status
   Optional<OrgMembership> findByOrganizationAndUserAndStatus(
           Organization organization, User user, InvitedStatus status
   );

//           check if user is member of org
    boolean existsByOrganizationAndUser(
            Organization organization, User user
    );
//    check if user has a membership row with a specific status in org
   boolean existsByOrganizationAndUserAndStatus(
           Organization organization, User user, InvitedStatus status
   );
//   get all orgs a user belongs to
    List<OrgMembership> findByUser(User user);

//    get all invites a user has with a given status
   List<OrgMembership>  findByUserAndStatus(User user, InvitedStatus status);

//   count members in org
    long countByOrganization(Organization organization);

 //    count members in org with a given status
   long countByOrganizationAndStatus(Organization organization, InvitedStatus status);
}
