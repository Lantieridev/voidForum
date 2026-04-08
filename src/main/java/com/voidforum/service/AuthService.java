package com.voidforum.service;

import com.voidforum.model.User;
import com.voidforum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public record RegisterResponse(String token, UserResponse user) {}
    public record UserResponse(String id, String username, String email) {}

    public RegisterResponse register(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId(), saved.getUsername());

        return new RegisterResponse(token, new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail()));
    }

    public RegisterResponse login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername());
        return new RegisterResponse(token, new UserResponse(user.getId(), user.getUsername(), user.getEmail()));
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
}