package com.voidforum.domain.service;

import com.voidforum.domain.model.User;
import com.voidforum.domain.port.in.CommentUseCase;
import com.voidforum.domain.port.in.PostUseCase;
import com.voidforum.domain.port.in.UserUseCase;
import com.voidforum.domain.port.out.UserRepositoryPort;
import com.voidforum.dto.UpdateNotificationsDto;
import com.voidforum.dto.UpdateProfileDto;
import com.voidforum.exception.ConflictException;
import com.voidforum.exception.ResourceNotFoundException;
import com.voidforum.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PostUseCase postUseCase;
    private final CommentUseCase commentUseCase;

    @Override
    public User registerUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        return userRepositoryPort.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepositoryPort.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Override
    public User findById(String id) {
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Override
    public User updateProfile(String username, UpdateProfileDto dto) {
        User user = findByUsername(username);

        if (dto.username() != null && !dto.username().equals(username)) {
            if (userRepositoryPort.findByUsername(dto.username()).isPresent()) {
                throw new ConflictException("El nombre de usuario ya está en uso");
            }
            user.setUsername(dto.username());
        }

        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            if (userRepositoryPort.findByEmail(dto.email()).isPresent()) {
                throw new ConflictException("El email ya está en uso");
            }
            user.setEmail(dto.email());
        }

        if (dto.displayName() != null) {
            user.setDisplayName(dto.displayName());
        }

        if (dto.bio() != null) {
            if (dto.bio().length() > 280) {
                throw new IllegalArgumentException("La bio no puede exceder 280 caracteres");
            }
            user.setBio(dto.bio());
        }

        return userRepositoryPort.save(user);
    }

    @Override
    public User changePassword(String username, String currentPassword, String newPassword) {
        User user = findByUsername(username);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("La contraseña actual es incorrecta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepositoryPort.save(user);
    }

    @Override
    public User updateNotifications(String username, UpdateNotificationsDto dto) {
        User user = findByUsername(username);
        user.setNotifyLikes(dto.notifyLikes());
        user.setNotifyComments(dto.notifyComments());
        user.setNotifyMentions(dto.notifyMentions());
        return userRepositoryPort.save(user);
    }

    @Override
    public void deleteAccount(String username, String password) {
        User user = findByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("La contraseña es incorrecta");
        }

        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String newUsername = "[deleted]-" + uniqueId;

        postUseCase.anonymizeUserPosts(username, newUsername);
        commentUseCase.anonymizeUserComments(username, newUsername);

        user.setUsername(newUsername);
        user.setEmail("[deleted]-" + uniqueId + "@deleted.local");
        user.setDisplayName(null);
        user.setBio(null);
        user.setPassword(null);
        userRepositoryPort.save(user);
    }

    @Override
    public void follow(String currentUsername, String targetUserId) {
        User currentUser = findByUsername(currentUsername);
        User targetUser = findById(targetUserId);

        if (currentUser.getId().equals(targetUserId)) {
            throw new IllegalArgumentException("No puedes seguirte a ti mismo");
        }

        if (currentUser.getFollowingIds() == null) {
            currentUser.setFollowingIds(new ArrayList<>());
        }

        if (currentUser.getFollowingIds().contains(targetUserId)) {
            throw new ConflictException("Ya sigues a este usuario");
        }

        currentUser.getFollowingIds().add(targetUserId);
        currentUser.setFollowingCount(currentUser.getFollowingCount() + 1);
        userRepositoryPort.save(currentUser);

        targetUser.setFollowerCount(targetUser.getFollowerCount() + 1);
        userRepositoryPort.save(targetUser);
    }

    @Override
    public void unfollow(String currentUsername, String targetUserId) {
        User currentUser = findByUsername(currentUsername);
        User targetUser = findById(targetUserId);

        if (currentUser.getFollowingIds() == null || !currentUser.getFollowingIds().contains(targetUserId)) {
            throw new IllegalArgumentException("No sigues a este usuario");
        }

        currentUser.getFollowingIds().remove(targetUserId);
        currentUser.setFollowingCount(Math.max(0, currentUser.getFollowingCount() - 1));
        userRepositoryPort.save(currentUser);

        targetUser.setFollowerCount(Math.max(0, targetUser.getFollowerCount() - 1));
        userRepositoryPort.save(targetUser);
    }

    @Override
    public boolean isFollowing(String currentUsername, String targetUserId) {
        User currentUser = findByUsername(currentUsername);
        return currentUser.getFollowingIds() != null && currentUser.getFollowingIds().contains(targetUserId);
    }

    @Override
    public List<User> getFollowers(String userId) {
        return userRepositoryPort.findByFollowingId(userId);
    }

    @Override
    public List<User> getFollowing(String userId) {
        User user = findById(userId);
        return userRepositoryPort.findAllById(user.getFollowingIds());
    }

    @Override
    public List<String> getFollowingIds(String username) {
        User user = findByUsername(username);
        return user.getFollowingIds() != null ? user.getFollowingIds() : new ArrayList<>();
    }

    @Override
    public User savePost(String userId, String postId) {
        User user = findById(userId);
        if (user.getSavedPosts() == null) {
            user.setSavedPosts(new ArrayList<>());
        }
        if (!user.getSavedPosts().contains(postId)) {
            user.getSavedPosts().add(postId);
            return userRepositoryPort.save(user);
        }
        return user;
    }

    @Override
    public User unsavePost(String userId, String postId) {
        User user = findById(userId);
        if (user.getSavedPosts() != null && user.getSavedPosts().contains(postId)) {
            user.getSavedPosts().remove(postId);
            return userRepositoryPort.save(user);
        }
        return user;
    }

    @Override
    public List<String> getSavedPosts(String userId) {
        User user = findById(userId);
        return user.getSavedPosts() != null ? user.getSavedPosts() : List.of();
    }
}
