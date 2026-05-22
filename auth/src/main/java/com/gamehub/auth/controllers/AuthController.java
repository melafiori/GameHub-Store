package com.gamehub.auth.controllers;

import com.gamehub.auth.models.Auth;
import com.gamehub.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Auth> save(@Valid @RequestBody Auth auth) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.authService.save(auth));
    }

    @PutMapping("/{id}")
    public  ResponseEntity<Auth> update(@PathVariable Long id, @Valid @RequestBody Auth auth) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.authService.updateById(id, auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.authService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
