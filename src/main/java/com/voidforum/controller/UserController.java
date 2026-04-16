package com.voidforum.controller;

import com.voidforum.dto.*;
import com.voidforum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.findByUsername(username);
            return ResponseEntity.ok(new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.getBio(),
                    user.isNotifyLikes(),
                    user.isNotifyComments(),
                    user.isNotifyMentions(),
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDto dto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.updateProfile(username, dto);
            return ResponseEntity.ok(new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.getBio(),
                    user.isNotifyLikes(),
                    user.isNotifyComments(),
                    user.isNotifyMentions(),
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.changePassword(username, dto.currentPassword(), dto.newPassword());
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me/notifications")
    public ResponseEntity<?> updateNotifications(@RequestBody UpdateNotificationsDto dto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            var user = userService.updateNotifications(username, dto);
            return ResponseEntity.ok(new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.getBio(),
                    user.isNotifyLikes(),
                    user.isNotifyComments(),
                    user.isNotifyMentions(),
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(@RequestBody Map<String, String> body) {
        try {
            String password = body.get("password");
            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Contraseña requerida para eliminar la cuenta"));
            }
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.deleteAccount(username, password);
            return ResponseEntity.ok(Map.of("message", "Cuenta eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            var user = userService.findById(id);
            return ResponseEntity.ok(new UserResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getDisplayName(),
                    user.getBio(),
                    user.isNotifyLikes(),
                    user.isNotifyComments(),
                    user.isNotifyMentions(),
                    user.getCreatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}