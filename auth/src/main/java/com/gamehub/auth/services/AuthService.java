package com.gamehub.auth.services;

import com.gamehub.auth.models.Auth;

import java.util.List;

public interface AuthService {
    List<Auth> getAll();
    Auth findById(Long id);
    Auth save(Auth auth);
    Auth updateById(Long id, Auth auth);
    void deleteById(Long id);
}
