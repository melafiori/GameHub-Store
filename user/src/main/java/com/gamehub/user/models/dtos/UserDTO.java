package com.gamehub.user.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @Email(message = "Correo inválido")
    private String email;
    private String telefono;
    private String rol;
    private String estado;
}
