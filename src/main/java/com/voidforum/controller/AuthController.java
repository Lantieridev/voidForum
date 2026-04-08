package com.voidforum.controller;

import com.voidforum.model.User;
import com.voidforum.service.AuthService;
import com.voidforum.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public record RegisterRequest(String username, String email, String password) {}
    public record LoginRequest(String username, String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            AuthService.RegisterResponse response = authService.register(
                    request.username(),
                    request.email(),
                    request.password()
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthService.RegisterResponse response = authService.login(request.username(), request.password());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(token);
            User user = authService.getUserById(userId);
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "avatar", user.getAvatar() != null ? user.getAvatar() : "",
                    "createdAt", user.getCreatedAt().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }
    }
}