package com.voidforum.repository;

import com.voidforum.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    // Fundamental para cargar los comentarios cuando alguien abre un post
    List<Comment> findByPostId(String postId);

    // Por si queremos ver el historial de comentarios de un usuario
    List<Comment> findByAuthorId(String authorId);
}