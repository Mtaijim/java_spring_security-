package com.example.Authx.security;

import com.example.Authx.entity.User;
import com.example.Authx.helper.UserHelper;
import com.example.Authx.repositories.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {
    private final userRepository userRepository;

    public boolean isSelf(Authentication authentication, String targetUserId){
        if(authentication == null || !authentication.isAuthenticated()){
            return false;
        }
        try {
            var targetUuid = UserHelper.parseUUID(targetUserId);
            String principalEmail = authentication.getName();
            return userRepository.findByEmail(principalEmail)
                    .map(User::getId)
                    .map(id->id.equals(targetUuid))
                    .orElse(false);
        }catch (IllegalArgumentException badUUid){
            return false;
        }
    }


}
