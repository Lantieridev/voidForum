package com.voidforum.repository;

import com.voidforum.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostIdOrderByCreatedAtDesc(String postId);
    Page<Comment> findByAuthorId(String authorId, Pageable pageable);
    long countByPostId(String postId);
}