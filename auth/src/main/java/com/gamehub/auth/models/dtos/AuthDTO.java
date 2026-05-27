package com.gamehub.auth.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@ToString
@Getter @Setter
@NoArgsConstructor
public class AuthDTO {

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El formato del email no es válido.")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String password;
}