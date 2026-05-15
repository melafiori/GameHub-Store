package com.gamehub.user.services;

import com.gamehub.user.models.User;
import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User save(User user);
    User updateById(Long id, User user);
    void deleteById(Long id);
    List<User> findByRol(String rol);
    List<User> findByEstado(String estado);
}
