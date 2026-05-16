package com.gamehub.auth.services;

import com.gamehub.auth.exceptions.AuthException;
import com.gamehub.auth.models.CuentaAcceso;
import com.gamehub.auth.repositories.AuthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServicelmpl implements AuthService{
    private final AuthRepository authRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public CuentaAcceso save(CuentaAcceso cuenta) {

        if(authRepository.findByEmail(cuenta.getEmail()).isPresent()) {
            throw new AuthException("Correo ya registrado");
        }
        cuenta.setPasswordHash(
                passwordEncoder.encode(cuenta.getPasswordHash())
        );

        cuenta.setEstado("ACTIVO");
        return authRepository.save(cuenta);
    }

    @Transactional
    @Override
    public CuentaAcceso updateById(Long cuentaId, CuentaAcceso cuenta) {

        CuentaAcceso currentCuenta = this.findById(cuentaId);

        currentCuenta.setRol(cuenta.getRol());

        currentCuenta.setEstado(cuenta.getEstado());

        if(cuenta.getPasswordHash() != null &&
                !cuenta.getPasswordHash().isEmpty()) {

            currentCuenta.setPasswordHash(
                    passwordEncoder.encode(cuenta.getPasswordHash())
            );
        }
        return authRepository.save(currentCuenta);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {

        CuentaAcceso cuenta = this.findById(id);

        cuenta.setEstado("INACTIVO");

        authRepository.save(cuenta);
    }

    @Transactional
    @Override
    public CuentaAcceso findById(Long id) {

        return authRepository.findById(id).orElseThrow(
                () -> new AuthException("Cuenta no encontrada"));
    }

    @Transactional
    @Override
    public CuentaAcceso findByEmail(String email) {

        return authRepository.findByEmail(email).orElseThrow(
                () -> new AuthException("Correo no encontrado"));
    }

    @Transactional
    @Override
    public List<CuentaAcceso> findAll() {
        return authRepository.findAll();
    }
}
