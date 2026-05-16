package com.gamehub.auth.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthDTO {
    @Email
    private String email;

    @NotBlank
    private String passwordHash;

    private String rol;
}
