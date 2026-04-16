package com.voidforum.service;

import com.voidforum.dto.UserLoginDto;
import com.voidforum.dto.UserRegisterDto;
import com.voidforum.dto.UserResponseDto;
import com.voidforum.model.User;
import com.voidforum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder; // <--- Nueva inyección

    public UserResponseDto register(UserRegisterDto request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // <--- ENCRIPTAMOS ACÁ
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getDisplayName(),
                savedUser.getBio(),
                savedUser.isNotifyLikes(),
                savedUser.isNotifyComments(),
                savedUser.isNotifyMentions(),
                savedUser.getCreatedAt()
        );
    }

    public Map<String, Object> login(UserLoginDto request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(user.getUsername());

        return Map.of(
                "token", token,
                "user", new UserResponseDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getDisplayName(),
                        user.getBio(),
                        user.isNotifyLikes(),
                        user.isNotifyComments(),
                        user.isNotifyMentions(),
                        user.getCreatedAt()
                )
        );
    }

    public Map<String, Object> getCurrentUser(String token) {
        if (!jwtService.validateToken(token)) {
            throw new RuntimeException("Token inválido o expirado");
        }

        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return Map.of(
                "token", token,
                "user", new UserResponseDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getDisplayName(),
                        user.getBio(),
                        user.isNotifyLikes(),
                        user.isNotifyComments(),
                        user.isNotifyMentions(),
                        user.getCreatedAt()
                )
        );
    }
}