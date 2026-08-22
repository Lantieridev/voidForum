package com.voidforum.infrastructure.persistence.mongo;

import com.voidforum.infrastructure.persistence.entity.PostDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoPostRepository extends MongoRepository<PostDocument, String> {
    List<PostDocument> findByAuthorId(String authorId);
    List<PostDocument> findByAuthorUsername(String authorUsername);

    @Query("{ $or: [ { 'content': { $regex: ?0, $options: 'i' } }, { 'tags': { $regex: ?0, $options: 'i' } } ] }")
    List<PostDocument> searchPosts(String query);

    @Query("{ 'tags': { $regex: ?0, $options: 'i' } }")
    List<PostDocument> searchByTag(String tag);

    @Query("{ 'authorUsername': { $regex: ?0, $options: 'i' } }")
    List<PostDocument> searchByAuthor(String username);

    @Query("{ 'content': { $regex: ?0, $options: 'i' } }")
    List<PostDocument> searchByContent(String content);

    @Query("{ 'authorId': { $in: ?0 } }")
    List<PostDocument> findByAuthorIdIn(List<String> authorIds);

    @Query("{ '_id': { $in: ?0 } }")
    List<PostDocument> findByIdIn(List<String> ids);
}
