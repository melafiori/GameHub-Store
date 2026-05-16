package com.gamehub.auth.services;

import com.gamehub.auth.exceptions.AuthException;
import com.gamehub.auth.models.Auth;
import com.gamehub.auth.repositories.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Override
    public List<Auth> getAll() {
        return this.authRepository.findAll();
    }

    @Override
    public Auth findById(Long id) {
        return this.authRepository.findById(id).orElseThrow(
                () -> new AuthException("Cuenta de acceso no encontrada.")
        );
    }

    @Override
    public Auth save(Auth auth) {
        if (this.authRepository.findById(auth.getAuthId()).isPresent()) {
            throw new AuthException("Cuenta de acceso ya existente.");
        }
        return this.authRepository.save(auth);
    }

    @Override
    public Auth updateById(Long id, Auth auth) {
        return this.authRepository.findById(id).map( element -> {
            element.setEmail(auth.getEmail());
            element.setRol(auth.getRol());
            element.setEstado(auth.getEstado());
            return this.authRepository.save(element);
        }).orElseThrow(
                () -> new AuthException("Cuenta de acceso no encontrada.")
        );
    }

    @Override
    public void deleteById(Long id) {
        this.authRepository.deleteById(id);
    }
}
