package com.example.Authx.services;

import com.example.Authx.entity.AppPermission;
import com.example.Authx.entity.User;

import com.example.Authx.repositories.permissionRepository;
import com.example.Authx.repositories.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class permissionService {
    private final permissionRepository permissionRepository;
    private final userRepository userRepository;

    //  Get all permissions
    public List<AppPermission> getAllPermissions(){
        return permissionRepository.findAll();
    }
   // Get permissions by category
    public List<AppPermission> getByCategory(String category){
        return permissionRepository.findByCategory(category);
    }
    //assign permission to user
    public void assignPermission(String userId , String permissionName) {

        User user = userRepository.findById(
                java.util.UUID.fromString(userId)
        ).orElseThrow(() -> new RuntimeException("user not found !!"));


        AppPermission found = permissionRepository
                .findByName(permissionName)
                .orElseThrow(() ->
                        new RuntimeException("Permission not found: " + permissionName));

        user.getPermissions().add(found);
        userRepository.save(user);

    }

//     remove permission from user
    public void removePermission(String userId, String PermissionName){
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(()-> new RuntimeException("user Not found "));

        user.getPermissions().removeIf(p->p.getName().equals(PermissionName));
        userRepository.save(user);
    }

//    check role user has permission

    public boolean hasPermission(User user , String permission ){
        boolean direct = user.getPermissions().stream()
                .anyMatch(p->p.getName().equals(permission));
        if(direct) return true;

        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p->p.getName().equals(permission));

    }
// create permission
    public AppPermission createPermission(String name, String description, String category)
    {
        if(permissionRepository.existsByName(name)){
            throw new RuntimeException("Permission already exist "+ name);
        }

        return permissionRepository.save(
                AppPermission.builder()
                        .name(name)
                        .description(description)
                        .category(category)
                        .build()
        );
    }
//  permission by id

public boolean hasPermissionById(
        String userId, String permissionName
)    {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(()->
                new RuntimeException("user not found "));
        return hasPermission(user,permissionName);
}

}
