package com.example.Authx.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Auth Application ",
                description = "Generic Authapp that can be used with any application"
        ,contact = @Contact(
                    name = "Mtaijim",
                email = "mhdtaijim9373@gmail.com"

),
                version = "1.0"
        ),
        security = {
                @SecurityRequirement(
                        name = "BearerAuth"
                )
        }

)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class APIDocConfig {

}
