package com.gamehub.user.controllers;

import com.gamehub.user.models.User;
import com.gamehub.user.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")

public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public User save(@Valid @RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/{id}")
    public User updateById(@PathVariable Long id, @RequestBody User user) {
        return userService.updateById(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) { userService.deleteById(id);
    }

    @GetMapping("/rol/{rol}")
    public List<User> findByRol(@PathVariable String rol) {
        return userService.findByRol(rol);
    }

    @GetMapping("/estado/{estado}")
    public List<User> findByEstado(@PathVariable String estado) {
        return userService.findByEstado(estado);
    }
}
