package com.voidforum.service;

import com.voidforum.model.User;
import com.voidforum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}