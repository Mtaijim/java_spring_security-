package com.example.Authx.config;



import com.example.Authx.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import java.util.Map;


import lombok.RequiredArgsConstructor;
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
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            )
         .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, e) -> {
                    e.printStackTrace();
                    response.setStatus(401);
                    response.setContentType("application/json");
                    String message = e.getMessage();

        Map<String, String> errorMap = Map.of(
                "Message", message,
                "Status", String.valueOf(401),
                "statusCode", Integer.toString(401)
        );
        var objectMapper = new ObjectMapper();
response.getWriter().write(objectMapper.writeValueAsString(errorMap));
    }
                )).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
}

}
