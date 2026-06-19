package com.example.Authx.security;


import com.example.Authx.exceptions.ResourceNotFoundException;
import com.example.Authx.repositories.userRepository;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class customUserDetailService implements UserDetailsService {
    private final  userRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
    return  userRepository.findByEmail(username).orElseThrow(()-> new BadCredentialsException("Invalid Email or password"));
    }
}
