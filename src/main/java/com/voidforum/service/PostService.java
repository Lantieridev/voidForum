package com.voidforum.service;

import com.voidforum.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostService {
    Page<Post> getPostsByTag(String tag, Pageable pageable);
    Page<Post> getAllPosts(Pageable pageable);
    Post getPostById(String id);
    String getUserVote(String postId, String userId);
    Post createPost(String title, String content, List<String> tags, String userId);
    Post updatePost(String id, String title, String content, List<String> tags, String userId);
    void deletePost(String id, String userId);
    VoteResult vote(String id, String type, String userId);

    // DTO para que el controlador pueda devolver el resultado del voto
    record VoteResult(int upvotes, int downvotes, String userVote) {}
}