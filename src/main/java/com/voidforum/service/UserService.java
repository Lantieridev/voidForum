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

    public void follow(String currentUsername, String targetUserId) {
        User currentUser = findByUsername(currentUsername);
        User targetUser = findById(targetUserId);

        if (currentUser.getId().equals(targetUserId)) {
            throw new RuntimeException("No puedes seguirte a ti mismo");
        }

        if (currentUser.getFollowingIds() == null) {
            currentUser.setFollowingIds(new java.util.ArrayList<>());
        }

        if (currentUser.getFollowingIds().contains(targetUserId)) {
            throw new RuntimeException("Ya sigues a este usuario");
        }

        currentUser.getFollowingIds().add(targetUserId);
        currentUser.setFollowingCount(currentUser.getFollowingCount() + 1);
        userRepository.save(currentUser);

        targetUser.setFollowerCount(targetUser.getFollowerCount() + 1);
        userRepository.save(targetUser);
    }

    public void unfollow(String currentUsername, String targetUserId) {
        User currentUser = findByUsername(currentUsername);
        User targetUser = findById(targetUserId);

        if (currentUser.getFollowingIds() == null || !currentUser.getFollowingIds().contains(targetUserId)) {
            throw new RuntimeException("No sigues a este usuario");
        }

        currentUser.getFollowingIds().remove(targetUserId);
        currentUser.setFollowingCount(Math.max(0, currentUser.getFollowingCount() - 1));
        userRepository.save(currentUser);

        targetUser.setFollowerCount(Math.max(0, targetUser.getFollowerCount() - 1));
        userRepository.save(targetUser);
    }

    public boolean isFollowing(String currentUsername, String targetUserId) {
        User currentUser = findByUsername(currentUsername);
        return currentUser.getFollowingIds() != null && currentUser.getFollowingIds().contains(targetUserId);
    }

    public List<User> getFollowers(String userId) {
        return userRepository.findByFollowingId(userId);
    }

    public List<User> getFollowing(String userId) {
        User user = findById(userId);
        return userRepository.findAllById(user.getFollowingIds());
    }

    public List<String> getFollowingIds(String username) {
        User user = findByUsername(username);
        return user.getFollowingIds() != null ? user.getFollowingIds() : new java.util.ArrayList<>();
    }
}