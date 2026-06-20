package com.gamehub.auth.controllers;

import com.gamehub.auth.models.Auth;
import com.gamehub.auth.models.dtos.AuthDTO;
import com.gamehub.auth.services.AuthService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/auth")
@Validated
@Tag(name="Auth V2", description = "Métodos CRUD para la gestión de Auth")
public class AuthControllerV2 {

    @Autowired
    private AuthService authService;


//    //SOLO EJEMPLO
//    @ApiResponse(responseCode = "200", description = "Opercación exitosa")
//    public ResponseEntity<CollectionModel<EntityModel<Auth>>> findAll(){
//        List<EntityModel<Auth>> entityModels = this.authService.findAll();
//        return ResponseEntity.ok(this.authService.findAll));
//    }


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
