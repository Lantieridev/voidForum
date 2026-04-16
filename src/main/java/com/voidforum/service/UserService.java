package com.voidforum.service;

import com.voidforum.dto.UpdateNotificationsDto;
import com.voidforum.dto.UpdateProfileDto;
import com.voidforum.model.User;
import com.voidforum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PostService postService;

    public User registerUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User updateProfile(String username, UpdateProfileDto dto) {
        User user = findByUsername(username);

        if (dto.username() != null && !dto.username().equals(username)) {
            if (userRepository.findByUsername(dto.username()).isPresent()) {
                throw new RuntimeException("El nombre de usuario ya está en uso");
            }
            user.setUsername(dto.username());
        }

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (userRepository.findByEmail(dto.email()).isPresent()) {
                throw new RuntimeException("El email ya está en uso");
            }
            user.setEmail(dto.email());
        }

        if (dto.displayName() != null) {
            user.setDisplayName(dto.displayName());
        }

        if (dto.bio() != null) {
            if (dto.bio().length() > 280) {
                throw new RuntimeException("La bio no puede exceder 280 caracteres");
            }
            user.setBio(dto.bio());
        }

        return userRepository.save(user);
    }

    public User changePassword(String username, String currentPassword, String newPassword) {
        User user = findByUsername(username);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User updateNotifications(String username, UpdateNotificationsDto dto) {
        User user = findByUsername(username);
        user.setNotifyLikes(dto.notifyLikes());
        user.setNotifyComments(dto.notifyComments());
        user.setNotifyMentions(dto.notifyMentions());
        return userRepository.save(user);
    }

    public void deleteAccount(String username, String password) {
        User user = findByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("La contraseña es incorrecta");
        }

        String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 8);
        String newUsername = "[deleted]-" + uniqueId;

        postService.anonymizeUserPosts(username, newUsername);

        user.setUsername(newUsername);
        user.setEmail("[deleted]-" + uniqueId + "@deleted.local");
        user.setDisplayName(null);
        user.setBio(null);
        user.setPassword(null);
        userRepository.save(user);
    }

    public User savePost(String userId, String postId) {
        User user = findById(userId);
        if (user.getSavedPosts() == null) {
            user.setSavedPosts(new java.util.ArrayList<>());
        }
        if (!user.getSavedPosts().contains(postId)) {
            user.getSavedPosts().add(postId);
            return userRepository.save(user);
        }
        return user;
    }

    public User unsavePost(String userId, String postId) {
        User user = findById(userId);
        if (user.getSavedPosts() != null && user.getSavedPosts().contains(postId)) {
            user.getSavedPosts().remove(postId);
            return userRepository.save(user);
        }
        return user;
    }

    public List<String> getSavedPosts(String userId) {
        User user = findById(userId);
        return user.getSavedPosts() != null ? user.getSavedPosts() : List.of();
    }
}
