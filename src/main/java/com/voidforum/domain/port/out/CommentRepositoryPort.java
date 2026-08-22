package com.voidforum.domain.port.out;

import com.voidforum.domain.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryPort {
    Comment save(Comment comment);
    List<Comment> saveAll(Iterable<Comment> comments);
    Optional<Comment> findById(String id);
    void deleteById(String id);
    boolean existsById(String id);
    List<Comment> findByPostId(String postId);
    List<Comment> findByAuthorUsername(String authorUsername);
    void deleteAllByPostId(String postId);
    List<Comment> findByParentCommentId(String parentCommentId);
    void deleteAllByParentCommentId(String parentCommentId);
}
