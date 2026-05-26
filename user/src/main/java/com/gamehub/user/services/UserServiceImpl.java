package com.gamehub.user.services;

import com.gamehub.user.exceptions.UserException;
import com.gamehub.user.models.Direccion;
import com.gamehub.user.models.User;
import com.gamehub.user.repositories.DireccionRepository;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gamehub.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DireccionRepository direccionRepository;


    //Guardar o registrar usuario
    @Transactional
    @Override
    public User save(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserException("Correo ya se encuentra registrado");
        }
        user.setEstado("ACTIVO");
        User savedUser = userRepository.save(user);
        if(user.getDirecciones() != null) {
            for (Direccion direccion : user.getDirecciones()) {
                direccion.setUserId(savedUser.getUserId());
                direccionRepository.save(direccion);
            }
        }
        return savedUser;
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
        return userRepository.findById(id).orElseThrow(
                () -> new UserException("Usuario con id: " + id + " no encontrado."));
    }

    @Transactional
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
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
