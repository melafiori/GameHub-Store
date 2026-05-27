package com.gamehub.auth.services;

import com.gamehub.auth.clients.UserClient;
import com.gamehub.auth.models.Auth;
import com.gamehub.auth.models.dtos.AuthDTO;
import com.gamehub.auth.repositories.AuthRepository;
import com.gamehub.auth.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthRepository authUserRepository;

    @Autowired
    private UserClient userFeignClient;

    @Override
    public String registrar(AuthDTO authDto) {
        try {
            userFeignClient.getByEmail(authDto.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("No se puede crear la credencial: El usuario no está registrado en el sistema de perfiles.");
        }

        if (authUserRepository.findByEmail(authDto.getEmail()).isPresent()) {
            throw new RuntimeException("Las credenciales para este correo ya existen.");
        }

        Auth authUser = new Auth();
        authUser.setEmail(authDto.getEmail());
        authUser.setPasswordHash(authDto.getPassword());
        authUser.setRol("CLIENTE");
        authUser.setEstado("ACTIVO");

        authUserRepository.save(authUser);

        return "Credenciales registradas exitosamente de forma lógica.";
    }

    @Override
    public String login(AuthDTO authDto) {
        Auth authUser = authUserRepository.findByEmail(authDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o credenciales incorrectas."));

        if (!authUser.getPasswordHash().equals(authDto.getPassword())) {
            throw new RuntimeException("Usuario no encontrado o credenciales incorrectas.");
        }

        return "Token simulado para prueba: " + authUser.getEmail().toUpperCase();
    }
}