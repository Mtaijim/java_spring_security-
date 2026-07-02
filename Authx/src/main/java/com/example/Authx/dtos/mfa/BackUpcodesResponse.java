package com.example.Authx.dtos.mfa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@Setter
public class BackUpcodesResponse {
    private List<String> codes;
}
