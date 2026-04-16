package com.voidforum.repository;

import com.voidforum.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByAuthorId(String authorId);
    List<Post> findByAuthorUsername(String authorUsername);
    List<Post> findByTagsIn(List<String> tags);
    
    @Query("{ $or: [ { 'content': { $regex: ?0, $options: 'i' } }, { 'tags': { $regex: ?0, $options: 'i' } } ] }")
    List<Post> searchPosts(String query);
    
    @Query("{ 'tags': { $regex: ?0, $options: 'i' } }")
    List<Post> searchByTag(String tag);
    
    @Query("{ 'authorUsername': { $regex: ?0, $options: 'i' } }")
    List<Post> searchByAuthor(String username);
    
    @Query("{ 'content': { $regex: ?0, $options: 'i' } }")
    List<Post> searchByContent(String content);

    @Query("{ 'id': { $in: ?0 } }")
    List<Post> findByIdIn(List<String> ids);
}
