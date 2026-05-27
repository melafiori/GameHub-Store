package com.gamehub.user.services;

import com.gamehub.user.models.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    User findByRut(String rut);
    User findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    User updateById(Long id, User user);
    void deleteById(Long id);
    List<User> findByRol(String rol);
    List<User> findByEstado(String estado);
}
