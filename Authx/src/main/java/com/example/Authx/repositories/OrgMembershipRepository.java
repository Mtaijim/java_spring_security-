package com.example.Authx.repositories;

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

    //       get specific membership
    Optional<OrgMembership> findByOrganizationAndUser(
            Organization organization, User user
    );

    //        check if user is member of org
    boolean existsByOrganizationAndUser(
            Organization organization, User user
    );

    //     get all orgs a user belongs to
    List<OrgMembership> findByUser(User user);

    //     count members in org
    long countByOrganization(Organization organization);
}
