package com.voidforum.repository;

import com.voidforum.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);
    List<Comment> findByAuthorUsername(String authorUsername);
    void deleteAllByPostId(String postId); // Para el borrado en cascada

    //Métodos para replies
    List<Comment> findByParentCommentId(String parentCommentId);
    void deleteAllByParentCommentId(String parentCommentId); // Borrar replies también
}