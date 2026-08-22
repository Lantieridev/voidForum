package com.voidforum.infrastructure.persistence.mongo;

import com.voidforum.infrastructure.persistence.entity.CommentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoCommentRepository extends MongoRepository<CommentDocument, String> {
    List<CommentDocument> findByPostId(String postId);
    List<CommentDocument> findByAuthorUsername(String authorUsername);
    void deleteAllByPostId(String postId);
    List<CommentDocument> findByParentCommentId(String parentCommentId);
    void deleteAllByParentCommentId(String parentCommentId);
}
