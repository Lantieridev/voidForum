package com.voidforum.service;

import com.voidforum.dto.UserLoginDto;
import com.voidforum.dto.UserRegisterDto;
import com.voidforum.dto.UserResponseDto;
import com.voidforum.exception.ConflictException;
import com.voidforum.exception.UnauthorizedException;
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
            throw new ConflictException("El nombre de usuario ya existe");
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
        // Mismo mensaje genérico para usuario inexistente y contraseña incorrecta:
        // distinguirlos permite enumerar usernames registrados.
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Usuario o contraseña incorrectos");
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
            throw new UnauthorizedException("Token inválido o expirado");
        }

        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Token inválido o expirado"));

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