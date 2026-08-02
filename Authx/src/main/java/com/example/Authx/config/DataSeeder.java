package com.example.Authx.config;


import com.example.Authx.entity.AppPermission;
import com.example.Authx.entity.Role;
import com.example.Authx.entity.RoleType;
import com.example.Authx.repositories.RoleRepository;
import com.example.Authx.repositories.permissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {


    private final permissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        seedPermissions();
        seedRoles();
    }


    private void seedPermissions() {

//        2d array object for name,desc,category

        Object[][] permissions = {
                // user management
                {"users_view",   "View all users",      "users"},
                {"users_edit",   "Edit any user",        "users"},
                {"users_delete", "Delete any user",      "users"},
                {"users_ban",    "Ban or unban users",   "users"},

                // reports
                {"reports_view",   "View reports",   "reports"},
                {"reports_export", "Export reports", "reports"},

                // billing
                {"billing_view",   "View billing info",   "billing"},
                {"billing_manage", "Manage billing",      "billing"},

                // audit
                {"audit_view", "View audit logs", "audit"},

                // settings
                {"settings_view",   "View settings",   "settings"},
                {"settings_manage", "Manage settings", "settings"},
        };

        for (Object[]p : permissions){
            String name = (String) p[0];
            if(!permissionRepository.existsByName(name))
            {
                permissionRepository.save(
                        AppPermission.builder()
                                .name(name)
                                .description((String) p[1])
                                .category((String) p[2])
                                .build()
                );
                log.info("Created permission: {}", name);
            }
        }

    }

    private void seedRoles() {
//         create Role_user if not present
        if(!roleRepository.existsByName(RoleType.ROLE_USER)){
            roleRepository.save(
                    Role.builder()
                            .name(RoleType.ROLE_USER).build()
            );
            log.info("Created role: ROLE_USER");
        }

// create Role_Admin if not exists
        if(!roleRepository.existsByName(RoleType.ROLE_ADMIN)){
            List<AppPermission> allPermissions =
                    permissionRepository.findAll();

            Role adminRole = Role.builder()
                    .name(RoleType.ROLE_ADMIN).build();
//  assign all permissions to admin role
            adminRole.getPermissions().addAll(allPermissions);
            roleRepository.save(adminRole);
            log.info("Created role: ROLE_ADMIN with all permissions");
        }

    }
}
