package com.example.Authx.dtos;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppPermissionDto {
    private Long id;
    private String name;
    private String description;
    private String category;
}
