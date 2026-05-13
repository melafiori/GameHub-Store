package com.gamehub.user.services;

import com.gamehub.user.models.User;
import com.gamehub.user.repositories.UserRepository;

public interface UserService {
    UserRepository findAll();
    User findById(Long id);
    User save(User user);
    void deleteById(Long id);
    User updateById(Long id, User user);
}
