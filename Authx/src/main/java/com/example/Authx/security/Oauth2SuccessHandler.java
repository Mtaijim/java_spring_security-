package com.example.Authx.security;

import com.example.Authx.entity.Provider;
import com.example.Authx.entity.RefreshToken;
import com.example.Authx.entity.User;
import com.example.Authx.repositories.RefreshTokenRepository;
import com.example.Authx.repositories.userRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final userRepository userRepository;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.auth.frontend.success-redirect}")
    private String frontEndSuccessUrl;


    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
       logger.info("Successful Authentication");
       logger.info(authentication.toString());
         OAuth2User OAuth2user=(OAuth2User)authentication.getPrincipal();
//         identify user
        String registrationId= "Unknown";
        if(authentication instanceof OAuth2AuthenticationToken token){
            registrationId = token.getAuthorizedClientRegistrationId();
        }

logger.info("registrationId:",registrationId);
        logger.info("user :"+OAuth2user.getAttributes().toString());
        User user ;
        switch (registrationId){
            case "google"-> {
                String googleid = OAuth2user.getAttributes().getOrDefault("sub","").toString();

                String emailId = OAuth2user.getAttributes().getOrDefault("email" ,"").toString();
                String name = OAuth2user.getAttributes().getOrDefault("name" ,"").toString();
                String picture = OAuth2user.getAttributes().getOrDefault("picture" ,"").toString();
                User newUser = User.builder()
                        .email(emailId)
                        .name(name)
                        .image(picture)
                        .enable(true)
                        .provider(Provider.GOOGLE)
                        .build();

            user = userRepository.findByEmail(emailId).orElseGet(()->userRepository.save(newUser));

            }
            case "github"->{

                String name = OAuth2user.getAttributes().getOrDefault("login","").toString();
                String githubId = OAuth2user.getAttributes().getOrDefault("id","").toString();
                String image = OAuth2user.getAttributes().getOrDefault("avatar_url","").toString();

                String email = (String) OAuth2user.getAttributes().get("email");
                if (email == null) {
                    email = name + "@github.com";
                }
                User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .image(image)
                        .enable(true)
                        .provider(Provider.GITHUB)
                        .providerId(githubId)
                        .build();
                user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(newUser));

            }


            default -> {
                throw new  RuntimeException("invalid registration id ");
            }
        }
        String jti =  UUID.randomUUID().toString();
       RefreshToken refreshTokenOb = RefreshToken.builder().jti(jti).user(user).revoked(false)
                        .createdAt(Instant.now())
                                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                                        .build();

       refreshTokenRepository.save(refreshTokenOb);
        String accessToken =  jwtService.generateAccessToken(user);
      String refreshToken =  jwtService.generateRefreshToken(user, refreshTokenOb.getJti());
cookieService.attachRefreshCookie(response,refreshToken, (int) jwtService.getRefreshTtlSeconds());
//        response.getWriter().write("Login successful");
        response.sendRedirect(frontEndSuccessUrl);

    }

}
