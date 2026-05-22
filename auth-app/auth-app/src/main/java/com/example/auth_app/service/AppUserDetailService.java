package com.example.auth_app.service;

import com.example.auth_app.entity.UserEntity;
import com.example.auth_app.repository.userRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService {
    private final userRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       UserEntity existingUser =  userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException(" user Not found for email "+ email));
       return new User(existingUser.getEmail(),existingUser.getPassword() ,new ArrayList<>());
    }
}
