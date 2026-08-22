package com.voidforum.infrastructure.persistence.adapter;

import com.voidforum.domain.model.Post;
import com.voidforum.domain.port.out.PostRepositoryPort;
import com.voidforum.infrastructure.persistence.entity.PostDocument;
import com.voidforum.infrastructure.persistence.mapper.PostMapper;
import com.voidforum.infrastructure.persistence.mongo.MongoPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class PostPersistenceAdapter implements PostRepositoryPort {

    private final MongoPostRepository mongoPostRepository;

    @Override
    public Post save(Post post) {
        PostDocument doc = PostMapper.toEntity(post);
        PostDocument saved = mongoPostRepository.save(doc);
        return PostMapper.toDomain(saved);
    }

    @Override
    public List<Post> saveAll(Iterable<Post> posts) {
        List<PostDocument> docs = StreamSupport.stream(posts.spliterator(), false)
                .map(PostMapper::toEntity)
                .collect(Collectors.toList());
        return mongoPostRepository.saveAll(docs).stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findAll() {
        return mongoPostRepository.findAll().stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Post> findById(String id) {
        return mongoPostRepository.findById(id).map(PostMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        mongoPostRepository.deleteById(id);
    }

    @Override
    public List<Post> findByAuthorId(String authorId) {
        return mongoPostRepository.findByAuthorId(authorId).stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findByAuthorUsername(String authorUsername) {
        return mongoPostRepository.findByAuthorUsername(authorUsername).stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> searchPosts(String query) {
        return mongoPostRepository.searchPosts(query).stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> searchByTag(String tag) {
        return mongoPostRepository.searchByTag(tag).stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> searchByAuthor(String username) {
        return mongoPostRepository.searchByAuthor(username).stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> searchByContent(String content) {
        return mongoPostRepository.searchByContent(content).stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findByAuthorIdIn(List<String> authorIds) {
        return mongoPostRepository.findByAuthorIdIn(authorIds).stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findByIdIn(List<String> ids) {
        return mongoPostRepository.findByIdIn(ids).stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findAllById(Iterable<String> ids) {
        return mongoPostRepository.findAllById(ids).stream()
                .map(PostMapper::toDomain)
                .collect(Collectors.toList());
    }
}
