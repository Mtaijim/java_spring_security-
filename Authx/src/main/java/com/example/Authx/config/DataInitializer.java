package com.example.Authx.config;

import com.example.Authx.entity.Role;
import com.example.Authx.entity.RoleType;
import com.example.Authx.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    @Override
    public void run(String... args) throws Exception {
        if(roleRepository.count()==0){
            roleRepository.save(Role.builder().name(RoleType.ROLE_USER).build());

            roleRepository.save(
                    Role.builder().name(RoleType.ROLE_ADMIN).build()
            );
        }
    }
}
