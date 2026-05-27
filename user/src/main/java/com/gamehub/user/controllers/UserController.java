package com.gamehub.user.controllers;

import com.gamehub.user.models.User;
import com.gamehub.user.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")

public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<User> save(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateById(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateById(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        this.userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<User>> findByRol(@PathVariable String rol) {
        return ResponseEntity.ok(userService.findByRol(rol));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<User>> findByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(userService.findByEstado(estado));
    }

    @GetMapping("/search")
    public ResponseEntity<User> getByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
