package com.voidforum.repository;

import com.voidforum.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findByAuthorId(String authorId, Pageable pageable);
    List<Post> findByTagsContaining(String tag);
    Page<Post> findByTagsContaining(String tag, Pageable pageable);
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}