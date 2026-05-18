package com.example.auth_app.io;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class profileRequest {

    @NotBlank(message = "Name should not be empty")
    private String name;
    @NotNull(message = "Email should not be Empty ")
    @Email(message = "Enter valid email address")
    private String email;
    @Size(min = 4 , message = "minimum should be atleast 4 characters ")
    private String password;


}
