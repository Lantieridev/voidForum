package com.voidforum.service;

import com.voidforum.model.Post;
import com.voidforum.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public Page<Post> getAllPosts(Pageable pageable) { return postRepository.findAll(pageable); }

    @Override
    public Page<Post> getPostsByTag(String tag, Pageable pageable) { return postRepository.findAll(pageable); }

    @Override
    public Post getPostById(String id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post no encontrado"));
    }

    @Override
    public String getUserVote(String postId, String userId) { return ""; }

    @Override
    public Post createPost(String title, String content, List<String> tags, String userId) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setTags(tags);
        post.setAuthorId(userId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpvotes(0);
        post.setDownvotes(0);
        return postRepository.save(post);
    }

    @Override
    public Post updatePost(String id, String title, String content, List<String> tags, String userId) {
        Post post = getPostById(id);
        post.setTitle(title);
        post.setContent(content);
        post.setTags(tags);
        return postRepository.save(post);
    }

    @Override
    public void deletePost(String id, String userId) { postRepository.deleteById(id); }

    @Override
    public VoteResult vote(String id, String type, String userId) { return new VoteResult(0, 0, type); }
}