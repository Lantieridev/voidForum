package com.voidforum.service;

import com.voidforum.model.User;
import com.voidforum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // Esto hace la inyección del Repository automáticamente
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(User user) {
        // Aquí podrías encriptar la password antes de guardar
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