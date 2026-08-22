package com.voidforum.infrastructure.persistence.adapter;

import com.voidforum.domain.model.User;
import com.voidforum.domain.port.out.UserRepositoryPort;
import com.voidforum.infrastructure.persistence.entity.UserDocument;
import com.voidforum.infrastructure.persistence.mapper.UserMapper;
import com.voidforum.infrastructure.persistence.mongo.MongoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final MongoUserRepository mongoUserRepository;

    @Override
    public User save(User user) {
        UserDocument doc = UserMapper.toEntity(user);
        UserDocument saved = mongoUserRepository.save(doc);
        return UserMapper.toDomain(saved);
    }

    @Override
    public List<User> saveAll(Iterable<User> users) {
        List<UserDocument> docs = StreamSupport.stream(users.spliterator(), false)
                .map(UserMapper::toEntity)
                .collect(Collectors.toList());
        return mongoUserRepository.saveAll(docs).stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return mongoUserRepository.findAll().stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(String id) {
        return mongoUserRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return mongoUserRepository.findByUsername(username).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoUserRepository.findByEmail(email).map(UserMapper::toDomain);
    }

    @Override
    public List<User> findByFollowingId(String userId) {
        return mongoUserRepository.findByFollowingId(userId).stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAllById(Iterable<String> ids) {
        return mongoUserRepository.findAllById(ids).stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }
}
