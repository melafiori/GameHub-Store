package com.gamehub.user.services;

import com.gamehub.user.exceptions.UserException;
import com.gamehub.user.models.User;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gamehub.user.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    //Guardar o registrar usuario
    @Transactional
    @Override
    public User save(User user) {

        if (this.userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserException("Usuario ya se encuentra registrado");
        }
        user.setEstado("ACTIVO");
        return this.userRepository.save(user);
    }

    //Actualizar usuario por id
    @Transactional
    @Override
    public User updateById(Long id, User user) {

        User currentUser = this.findById(id);
        currentUser.setNombre(user.getNombre());
        currentUser.setTelefono(user.getTelefono());
        currentUser.setRol(user.getRol());

        return this.userRepository.save(currentUser);
    }

    //Eliminar user por id
    @Transactional
    @Override
    public void deleteById(Long id) {

        User user = this.findById(id);
        user.setEstado("INACTIVO");

        this.userRepository.save(user);
    }

    //Buscar user por id
    @Transactional(readOnly = true)
    @Override
    public User findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(
                () -> new UserException("Usuario con id: " + id + " no encontrado.")
        );
    }

    //Buscar usuario por rol
    @Transactional
    @Override
    public List<User> findByRol(String rol) {
        return this.userRepository.findByRol(rol);
    }

    //Buscar usuario por estado
    @Transactional
    @Override
    public List<User> findByEstado(String estado) {
        return this.userRepository.findByEstado(estado);
    }
}
