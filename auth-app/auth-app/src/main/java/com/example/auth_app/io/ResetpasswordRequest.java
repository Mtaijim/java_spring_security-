package com.example.auth_app.io;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.authenticator.SavedRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetpasswordRequest {
    @NotBlank(message = "Enter Email")
    private String email;
    @NotBlank(message = "password is required")
    private String newPassword;
    private String otp;


}
