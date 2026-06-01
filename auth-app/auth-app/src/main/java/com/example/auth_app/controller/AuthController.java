package com.example.auth_app.controller;

import com.example.auth_app.io.*;
import com.example.auth_app.service.AppUserDetailService;
import com.example.auth_app.service.profileService;
import com.example.auth_app.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailService appUserDetailService;
    private final JwtUtil jwtUtil;
    private final profileService profileService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){


        try {
            authenticate(request.getEmail(),request.getPassword());
            final UserDetails  userDetails=  appUserDetailService.loadUserByUsername(request.getEmail());
           final String jwtToken = jwtUtil.generateToken(userDetails);
            ResponseCookie cookie = ResponseCookie.from("jwt",jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString() )
                    .body(new AuthResponse(request.getEmail(),jwtToken));


        }catch (BadCredentialsException ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message","Email or passWord is invlaid ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

        }catch (DisabledException ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message","Account is Disabled  ");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        }catch (Exception ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message","Authentication failed ");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }



    private void authenticate(String email, String password) {
authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));

    }

    @GetMapping("/is_Authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name")String email){
return ResponseEntity.ok(email !=null);
    }

    @PostMapping("/send-reset-otp")
    public void sendResetOtp(@RequestParam String email){
        try{
            profileService.sendResetOtp(email);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());

        }

    }
    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetpasswordRequest request){
 try{
     profileService.resetPassword(request.getEmail(),request.getOtp(),request.getNewPassword());


 }catch (Exception e){
     throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());

 }
    }
    @PostMapping("/Send-otp")
    public void sendVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name")String email){
        try {
            profileService.sendOtp(email);

        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());

        }
    }
    @PostMapping("/verify-otp")
    public void verifyEmail(@RequestBody Map<String,Object> request, @CurrentSecurityContext(expression = "authentication?.name" )String email){

        if(request.get("otp").toString()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"missing details");
        }
        try{
      profileService.verifyOtp(email,request.get("otp").toString());

  }catch (Exception e){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());

  }
    }
}
