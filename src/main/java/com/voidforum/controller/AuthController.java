package com.voidforum.controller;

import com.voidforum.dto.UserLoginDto;
import com.voidforum.dto.UserRegisterDto;
import com.voidforum.dto.UserResponseDto;
import com.voidforum.exception.UnauthorizedException;
import com.voidforum.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterDto request) {
        UserResponseDto response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginDto request) {
        Map<String, Object> response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Token no proporcionado");
        }
        String token = authHeader.substring(7);
        Map<String, Object> response = authService.getCurrentUser(token);
        return ResponseEntity.ok(response);
    }
}
