package com.voidforum.controller;

import com.voidforum.model.User;
import com.voidforum.service.AuthService;
import com.voidforum.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    // --- DTOs (Records) de Entrada ---
    public record RegisterRequest(String username, String email, String password) {}
    public record LoginRequest(String username, String password) {}

    // --- DTOs (Records) de Salida ---
    public record ErrorResponse(String error) {}

    // Molde seguro: Garantiza que NUNCA se filtre la contraseña al frontend
    public record UserResponse(String id, String username, String email, String avatar, String createdAt) {
        public static UserResponse fromUser(User user) {
            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getAvatar() != null ? user.getAvatar() : "",
                    user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
            );
        }
    }

    // --- ENDPOINTS ---

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // El servicio se encarga de hashear la contraseña y guardar en MongoDB
            AuthService.RegisterResponse response = authService.register(
                    request.username(),
                    request.email(),
                    request.password()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // El servicio verifica la contraseña y genera el JWT
            AuthService.RegisterResponse response = authService.login(request.username(), request.password());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtService.extractUserId(token);
            User user = authService.getUserById(userId);

            // Devolvemos los datos pasados por nuestro filtro seguro
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid token"));
        }
    }
}