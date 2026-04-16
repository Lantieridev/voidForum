package com.voidforum.repository;

import com.voidforum.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);
    void deleteAllByPostId(String postId); // Para el borrado en cascada
}