package com.example.auth_app.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class profileResponse {
    private String userId;
    private String name;
    private String email;
    private Boolean isAccountVerified;



}
