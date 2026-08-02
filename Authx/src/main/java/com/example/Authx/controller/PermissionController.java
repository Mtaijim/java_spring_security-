package com.example.Authx.controller;

import com.example.Authx.entity.AppPermission;
import com.example.Authx.services.permissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final permissionService permissionService;

//    get all permissions

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppPermission>> getAll(){
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

//    get permissions by category

    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppPermission>> getByCategory(@PathVariable String category){
        return ResponseEntity.ok(permissionService.getByCategory(category));
    }



//    create new permissions
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppPermission> create(@RequestBody Map<String,String> body){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                permissionService.createPermission(
                        body.get("name"),
                        body.get("description"),
                        body.get("category")
                )
        );


    }
//         Assign Permission
    @PostMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assign(@PathVariable String userId,
                                         @RequestBody Map<String, String> body ){
        permissionService.assignPermission(
                userId,body.get("permission")
        );
        return ResponseEntity.ok("permission assigned SuccessFully ");
    }


// delete /remove permission
@DeleteMapping("/users/{userId}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<String> remove(
        @PathVariable String userId,
        @RequestBody Map<String, String> body) {

    permissionService.removePermission(
            userId, body.get("permission")
    );
    return ResponseEntity.ok("Permission removed successfully");
}


// check

    @GetMapping("/users/{userId}/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> check(
            @PathVariable String userId,
            @RequestParam String permission) {

        boolean has = permissionService
                .hasPermissionById(userId, permission);

        return ResponseEntity.ok(Map.of("hasPermission", has));
    }



}
