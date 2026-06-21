package com.gamehub.auth.controllers;

import com.gamehub.auth.models.dtos.AuthDTO;
import com.gamehub.auth.services.AuthService;
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


    @PostMapping("/register")
    @Operation(summary = "Registro de un nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente.")
    public ResponseEntity<String> registrar(@Valid @RequestBody AuthDTO authDTO) {
        try {
            String respuesta = authService.registrar(authDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
