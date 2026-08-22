package com.voidforum.domain.service;

import com.voidforum.domain.model.User;
import com.voidforum.domain.port.in.AuthUseCase;
import com.voidforum.domain.port.out.UserRepositoryPort;
import com.voidforum.dto.UserLoginDto;
import com.voidforum.dto.UserRegisterDto;
import com.voidforum.dto.UserResponseDto;
import com.voidforum.exception.ConflictException;
import com.voidforum.exception.UnauthorizedException;
import com.voidforum.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegisterDto request) {
        if (userRepositoryPort.findByUsername(request.username()).isPresent()) {
            throw new ConflictException("El nombre de usuario ya existe");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepositoryPort.save(user);

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

    @Override
    public Map<String, Object> login(UserLoginDto request) {
        User user = userRepositoryPort.findByUsername(request.username())
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

    @Override
    public Map<String, Object> getCurrentUser(String token) {
        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Token inválido o expirado");
        }

        String username = jwtService.extractUsername(token);
        User user = userRepositoryPort.findByUsername(username)
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
