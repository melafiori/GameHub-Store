package com.gamehub.auth.models.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrationRequest(@NotBlank String nombre, @NotBlank String rut,
        @NotBlank @Email String email, @NotBlank String telefono, @NotBlank String password,
        @Valid @NotNull Address direccion) {
    public record Address(@NotBlank String calle, @NotNull @Positive Integer numero,
                          @NotBlank String comuna, @NotBlank String ciudad) {}
}
