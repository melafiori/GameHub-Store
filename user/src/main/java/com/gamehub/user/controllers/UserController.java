package com.gamehub.user.controllers;

import com.gamehub.user.models.User;
import com.gamehub.user.services.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Controlador Usuario", description = "Gestiona las operaciones relacionadas con los usuarios de GameHub")

public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista con todos los usuarios registrados.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente")})
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Obtiene un usuario específico mediante su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.userService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<User> save(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actuaiza los datos de un usuario existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> updateById(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateById(id, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario mediante su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Uusario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        this.userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Buscar usuarios por rol", description = "Obtiene todos los usuarios que poseen un rol específico")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Usuarios encontrados correctamente")})
    public ResponseEntity<List<User>> findByRol(@PathVariable String rol) {
        return ResponseEntity.ok(userService.findByRol(rol));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Buscar usuarios por estado", description = "Obtiene todos los usuarios según su estado")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Usuarios encontrados correctamente")})
    public ResponseEntity<List<User>> findByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(userService.findByEstado(estado));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar usuario por correo", description = "Obtiene un usuario utilizando su correo eléctronico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> getByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
