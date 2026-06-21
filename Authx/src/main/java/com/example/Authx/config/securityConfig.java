package com.example.Authx.config;



import com.example.Authx.dtos.ApiError;
import com.example.Authx.security.JwtAuthenticationFilter;
import com.example.Authx.security.Oauth2SuccessHandler;
import com.nimbusds.oauth2.sdk.auth.JWTAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;



import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class securityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }


    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final Oauth2SuccessHandler successHandler;

  @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login",
                    "/api/v1/auth/logout","/api/v1/auth/refresh")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            )
            .oauth2Login(oauth2-> oauth2.successHandler(successHandler)
                    .failureHandler(null))
            .logout(AbstractHttpConfigurer::disable)
         .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, e) -> {
                    e.printStackTrace();
                    response.setStatus(401);
                    response.setContentType("application/json");

                    String error = (String) request.getAttribute("error");

                     String message = error != null
                             ? error
                             : "Unauthorized Access ! " + e.getMessage();

                     var apiError = ApiError.of(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", message, request.getRequestURI(), true);
        var objectMapper = new ObjectMapper();
response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
                )).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
}

}
