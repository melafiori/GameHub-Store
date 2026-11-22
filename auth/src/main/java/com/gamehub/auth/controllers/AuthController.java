package com.gamehub.auth.controllers;

import com.gamehub.auth.models.dtos.AuthDTO;
import com.gamehub.auth.services.AuthService;
import com.gamehub.auth.services.AuthServiceImpl;
import com.gamehub.auth.models.dtos.RegistrationRequest;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name="Auth V1", description = "Métodos CRUD para la gestión de Auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthServiceImpl registrationService;


    @PostMapping("/register")
    @Operation(summary = "Registro de un nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente.")
    public ResponseEntity<String> registrar(@Valid @RequestBody RegistrationRequest request) {
        try {
            registrationService.registrarNuevo(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registro completado");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (FeignException e) {
            return ResponseEntity.status(e.status() == 409 ? HttpStatus.CONFLICT : HttpStatus.SERVICE_UNAVAILABLE)
                    .body(e.status() == 409 ? "Correo o RUT ya registrado" : "No se pudo completar el registro; reintenta con los mismos datos");
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Inicio de sesión de usuario", description = "Inicia sesión a un usuario en el sistema")
    @ApiResponse(responseCode = "200", description = "Sesión iniciada correctamente.")
    public ResponseEntity<String> login(@Valid @RequestBody AuthDTO authDTO) {
        try {
            String tokenSimulado = authService.login(authDTO);
            return ResponseEntity.status(HttpStatus.OK).body(tokenSimulado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
