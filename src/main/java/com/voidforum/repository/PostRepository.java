package com.voidforum.repository;

import com.voidforum.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    // Para filtrar posts por un tag específico en el foro
    List<Post> findByTagsContaining(String tag);

    // Para mostrar todos los posts de un usuario en su perfil
    List<Post> findByAuthorId(String authorId);
}