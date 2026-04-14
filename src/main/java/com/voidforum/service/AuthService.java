package com.voidforum.service;

import com.voidforum.model.User;

public interface AuthService {
    RegisterResponse register(String username, String email, String password);
    RegisterResponse login(String username, String password);
    User getUserById(String userId);

    // DTO que el controlador AuthController espera recibir
    record RegisterResponse(String token, User user) {}
}