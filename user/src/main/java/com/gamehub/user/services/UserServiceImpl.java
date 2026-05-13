package com.gamehub.user.services;

import com.gamehub.user.exceptions.UserException;
import com.gamehub.user.models.User;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.gamehub.user.repositories.UserRepository;

import java.util.List;

public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {return this.userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(
                () -> new UserException("Usuario con id: " + id + " no encontrado.")
        );
    }

    @Override
    public User save(User user){
        if(this.userRepository.findById(user.getUserId()).isPresent()){
            throw new UserException("Usuario con ID: " + user.getUserId() + " ya existe.");
        }
        return this.userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public User updateById(Long id, User user) {
        return null;
    }
}
