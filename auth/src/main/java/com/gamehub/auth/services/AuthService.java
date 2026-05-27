package com.gamehub.auth.services;

import com.gamehub.auth.models.Auth;
import com.gamehub.auth.models.dtos.AuthDTO;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;


public interface AuthService {
    String registrar(AuthDTO authDto);
    String login(AuthDTO authDto);
}
