package com.voidforum.domain.port.out;

import com.voidforum.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    List<User> saveAll(Iterable<User> users);
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByFollowingId(String userId);
    List<User> findAllById(Iterable<String> ids);
}
