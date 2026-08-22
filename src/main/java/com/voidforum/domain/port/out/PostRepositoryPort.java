package com.voidforum.domain.port.out;

import com.voidforum.domain.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryPort {
    Post save(Post post);
    List<Post> saveAll(Iterable<Post> posts);
    List<Post> findAll();
    Optional<Post> findById(String id);
    void deleteById(String id);
    List<Post> findByAuthorId(String authorId);
    List<Post> findByAuthorUsername(String authorUsername);
    List<Post> searchPosts(String query);
    List<Post> searchByTag(String tag);
    List<Post> searchByAuthor(String username);
    List<Post> searchByContent(String content);
    List<Post> findByAuthorIdIn(List<String> authorIds);
    List<Post> findByIdIn(List<String> ids);
    List<Post> findAllById(Iterable<String> ids);
}
