package com.voidforum.infrastructure.persistence.adapter;

import com.voidforum.domain.model.Comment;
import com.voidforum.domain.port.out.CommentRepositoryPort;
import com.voidforum.infrastructure.persistence.entity.CommentDocument;
import com.voidforum.infrastructure.persistence.mapper.CommentMapper;
import com.voidforum.infrastructure.persistence.mongo.MongoCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class CommentPersistenceAdapter implements CommentRepositoryPort {

    private final MongoCommentRepository mongoCommentRepository;

    @Override
    public Comment save(Comment comment) {
        CommentDocument doc = CommentMapper.toEntity(comment);
        CommentDocument saved = mongoCommentRepository.save(doc);
        return CommentMapper.toDomain(saved);
    }

    @Override
    public List<Comment> saveAll(Iterable<Comment> comments) {
        List<CommentDocument> docs = StreamSupport.stream(comments.spliterator(), false)
                .map(CommentMapper::toEntity)
                .collect(Collectors.toList());
        return mongoCommentRepository.saveAll(docs).stream()
                .map(CommentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Comment> findById(String id) {
        return mongoCommentRepository.findById(id).map(CommentMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        mongoCommentRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mongoCommentRepository.existsById(id);
    }

    @Override
    public List<Comment> findByPostId(String postId) {
        return mongoCommentRepository.findByPostId(postId).stream()
                .map(CommentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByAuthorUsername(String authorUsername) {
        return mongoCommentRepository.findByAuthorUsername(authorUsername).stream()
                .map(CommentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByPostId(String postId) {
        mongoCommentRepository.deleteAllByPostId(postId);
    }

    @Override
    public List<Comment> findByParentCommentId(String parentCommentId) {
        return mongoCommentRepository.findByParentCommentId(parentCommentId).stream()
                .map(CommentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByParentCommentId(String parentCommentId) {
        mongoCommentRepository.deleteAllByParentCommentId(parentCommentId);
    }
}
