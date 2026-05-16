package com.gamehub.auth.controllers;

import com.gamehub.auth.models.Auth;
import com.gamehub.auth.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auths")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<Auth>> findAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.authService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auth> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.authService.findById(id));
    }
}
