package com.voidforum.repository;

import com.voidforum.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByAuthorId(String authorId);
    List<Post> findByAuthorUsername(String authorUsername);
    List<Post> findByTagsIn(List<String> tags); // Para buscar por etiquetas
}