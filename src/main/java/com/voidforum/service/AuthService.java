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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserResponseDto register(UserRegisterDto request) {
        // Validar si el username ya está en uso
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Mapear de DTO a Entidad (User) usando el Builder de Lombok
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password()) // NOTA: Aquí iría el BCrypt más adelante
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // Retornar el DTO de respuesta (sin la password)
        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    public Map<String, Object> login(UserLoginDto request) {
        // Buscar el usuario o lanzar error si no existe
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Comparar contraseñas
        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generar el Token JWT
        String token = jwtService.generateToken(user.getUsername());

        // Devolver el token y los datos del usuario en un Map
        return Map.of(
                "token", token,
                "user", new UserResponseDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreatedAt()
                )
        );
    }
}