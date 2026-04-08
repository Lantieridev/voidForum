package com.voidforum.service;

import com.voidforum.model.Post;
import com.voidforum.model.User;
import com.voidforum.model.Vote;
import com.voidforum.repository.PostRepository;
import com.voidforum.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final UserService userService;

    public Post createPost(String title, String content, List<String> tags, String userId) {
        User user = userService.getUserById(userId);

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setTags(tags);
        post.setAuthorId(userId);
        post.setAuthorUsername(user.getUsername());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setUpvotes(0);
        post.setDownvotes(0);

        return postRepository.save(post);
    }

    public Post getPostById(String id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Post> getPostsByTag(String tag, Pageable pageable) {
        return postRepository.findByTagsContaining(tag, pageable);
    }

    public Post updatePost(String id, String title, String content, List<String> tags, String userId) {
        Post post = getPostById(id);
        
        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this post");
        }

        if (title != null) post.setTitle(title);
        if (content != null) post.setContent(content);
        if (tags != null) post.setTags(tags);
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    public void deletePost(String id, String userId) {
        Post post = getPostById(id);
        
        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    public record VoteResult(int upvotes, int downvotes, String userVote) {}

    public VoteResult vote(String postId, String voteType, String userId) {
        Post post = getPostById(postId);
        
        Optional<Vote> existingVote = voteRepository.findByUserIdAndTargetIdAndTargetType(userId, postId, "post");
        
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            
            if (vote.getVoteType().equals(voteType)) {
                voteRepository.delete(vote);
                updatePostVotes(post);
                return new VoteResult(post.getUpvotes(), post.getDownvotes(), null);
            }
            
            vote.setVoteType(voteType);
            voteRepository.save(vote);
            updatePostVotes(post);
        } else {
            Vote vote = new Vote();
            vote.setUserId(userId);
            vote.setTargetId(postId);
            vote.setTargetType("post");
            vote.setVoteType(voteType);
            vote.setCreatedAt(LocalDateTime.now());
            voteRepository.save(vote);
            updatePostVotes(post);
        }

        return new VoteResult(post.getUpvotes(), post.getDownvotes(), voteType);
    }

    private void updatePostVotes(Post post) {
        int upvotes = (int) voteRepository.countByTargetIdAndVoteType(post.getId(), "up");
        int downvotes = (int) voteRepository.countByTargetIdAndVoteType(post.getId(), "down");
        post.setUpvotes(upvotes);
        post.setDownvotes(downvotes);
        postRepository.save(post);
    }

    public String getUserVote(String postId, String userId) {
        return voteRepository.findByUserIdAndTargetIdAndTargetType(userId, postId, "post")
                .map(Vote::getVoteType)
                .orElse(null);
    }
}