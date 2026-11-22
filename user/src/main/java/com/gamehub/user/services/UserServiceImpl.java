package com.gamehub.user.services;

import com.gamehub.user.exceptions.UserException;
import com.gamehub.user.models.Direccion;
import com.gamehub.user.models.User;
import com.gamehub.user.repositories.DireccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gamehub.user.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Transactional
    public User registerOnce(String registrationId, User user) {
        return userRepository.findByRegistrationId(registrationId).orElseGet(() -> {
            user.setUserId(null);
            user.setRegistrationId(registrationId);
            user.setRol("CLIENTE");
            user.setEstado("ACTIVO");
            return save(user);
        });
    }


    //Guardar o registrar usuario
    @Transactional
    @Override
    public User save(User usuario) {
        // Regla de negocio: Validar duplicidad de RUT
        if (userRepository.findByRut(usuario.getRut()).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "El RUT ya se encuentra registrado.");
        }

        // Regla de negocio: Validar duplicidad de Correo
        if (userRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "El correo electrónico ya está en uso.");
        }

        List<Direccion> direcciones = usuario.getDirecciones();
        User guardado = userRepository.save(usuario);
        if (direcciones != null) {
            for (Direccion direccion : direcciones) {
                direccion.setId(null);
                direccion.setUserId(guardado.getUserId());
                direccionRepository.save(direccion);
            }
            guardado.setDirecciones(direcciones);
        }
        return guardado;
    }

    //Actualizar usuario por id
    @Transactional
    @Override
    public User updateById(Long id, User user) {

        User currentUser = this.findById(id);
        currentUser.setNombre(user.getNombre());
        currentUser.setTelefono(user.getTelefono());
        currentUser.setRol(user.getRol());

        return userRepository.save(currentUser);
    }

    //Eliminar user por id
    @Transactional
    @Override
    public void deleteById(Long id) {

        User user = this.findById(id);
        user.setEstado("INACTIVO");
        userRepository.save(user);
    }

    //Buscar user por id
    @Transactional
    @Override
    public User findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserException("Usuario con id: " + id + " no encontrado."));
        user.setDirecciones(direccionRepository.findByUserId(id));
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByRut(String rut) {
        return userRepository.findByRut(rut).orElseThrow(
                () -> new UserException("Usuario con rut: " + rut + " no encontrado.")
        );
    }

    //Buscar usuario por rol
    @Transactional
    @Override
    public List<User> findByRol(String rol) {
        return userRepository.findByRol(rol);
    }

    //Buscar usuario por estado
    @Transactional
    @Override
    public List<User> findByEstado(String estado) {
        return userRepository.findByEstado(estado);
    }

}
