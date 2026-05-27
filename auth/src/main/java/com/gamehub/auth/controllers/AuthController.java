package com.gamehub.auth.controllers;

import com.gamehub.auth.models.Auth;
import com.gamehub.auth.models.dtos.AuthDTO;
import com.gamehub.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registrar(@Valid @RequestBody AuthDTO authDTO) {
        try {
            String respuesta = authService.registrar(authDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthDTO authDTO) {
        try {
            String tokenSimulado = authService.login(authDTO);
            return ResponseEntity.status(HttpStatus.OK).body(tokenSimulado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
