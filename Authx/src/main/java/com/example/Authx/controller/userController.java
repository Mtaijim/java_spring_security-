package com.example.Authx.controller;

import com.example.Authx.dtos.UserDto;
import com.example.Authx.services.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class userController {
private final UserService userService;

        @PostMapping()
        public ResponseEntity<UserDto> createUser (@RequestBody UserDto userDto){
return ResponseEntity.status(HttpStatus.CREATED).body((userService.createUser(userDto)));
    }

    @GetMapping()
    public ResponseEntity<Iterable<UserDto>> getAllUsers(){
            return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email){
            return ResponseEntity.ok(userService.getUserByEmail(email));
    }

//delete user by id
    @DeleteMapping("/{uuid}")
    public void deleteUser(@PathVariable("uuid") String uuid){
            userService.deleteUser(uuid);
    }
    // update user by id
    @PutMapping("/{uuid}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto,@PathVariable("uuid") String uuid){
            return ResponseEntity.ok(userService.updateUser(userDto,uuid));
    }
// get user by id
    @GetMapping("/{userid}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userid") String userid){
            return ResponseEntity.ok(userService.getUserById(userid));
    }
}
